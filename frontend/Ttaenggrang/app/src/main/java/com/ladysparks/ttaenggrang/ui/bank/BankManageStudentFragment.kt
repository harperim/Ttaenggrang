package com.ladysparks.ttaenggrang.ui.bank

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.app.viewmodel.BankViewModel
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.base.BaseTableAdapter
import com.ladysparks.ttaenggrang.data.model.dto.BankManageDto
import com.ladysparks.ttaenggrang.databinding.DialogAccountDetailBinding
import com.ladysparks.ttaenggrang.databinding.FragmentBankManageStudentBinding
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel
import com.ladysparks.ttaenggrang.util.NumberUtil


class BankManageStudentFragment : BaseFragment<FragmentBankManageStudentBinding>(
    FragmentBankManageStudentBinding::bind,
    R.layout.fragment_bank_manage_student
) {

    private val viewModel: BankViewModel by viewModels()
    private lateinit var tableAdapter: BaseTableAdapter
    private val columnWeights = listOf(0.5f, 1f, 0.7f, 0.7f, 0.7f, 1f, 1.2f,1.2f,1.2f)
    private var savingsList: List<BankManageDto> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //초기화
        initAdapter()
        observeViewModel()

        // 서버에서 데이터 가져오기
        viewModel.fetchUserSavings()
        viewModel.fetchUserSavingCount()
    }

    private fun initAdapter() {
        tableAdapter = BaseTableAdapter(
            header = listOf(
                "No.",
                "상품명",
                "상품 유형",
                "가입 기간",
                "이자율",
                "납입금액",
                "가입일",
                "만기일",
                "예상 지급액"
            ), // ✅ 헤더 컬럼 설정
            data = emptyList(), // ✅ 초기 데이터 없음
            columnWeights = columnWeights,
            onRowClickListener = { rowIndex, _ ->
                val savingsId = savingsList[rowIndex].savingsProductId // ✅ 클릭된 적금 ID 가져오기
                showBankHistoryDialog(savingsId)
            }
        )
        binding.recyclerBankManageList.adapter = tableAdapter
    }

    private fun showBankHistoryDialog(savingsSubscriptionId: Int) {
        viewModel.fetchBankHistory(savingsSubscriptionId)

        val dialogBinding = DialogAccountDetailBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.55).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val tableAdapter = BaseTableAdapter(
            header = listOf("날짜", "거래내역", "금액", "이자율", "잔액"),
            data = emptyList(),
            columnWeights = listOf(1f, 1.5f, 1f, 1f, 1.5f)
        )
        dialogBinding.recyclerAccountHistory.adapter = tableAdapter

        viewModel.bankHistory.observe(viewLifecycleOwner) { bankHistory ->
            bankHistory?.let {
                dialogBinding.textDialogTitle.text = it.savingsName
                dialogBinding.textDialogContent.text = "시작일: ${it.startDate}"
                dialogBinding.textDialogContent2.text = "만기일: ${it.endDate}"
                dialogBinding.textPayoutAmount2.text = "예상 지급액:   ${NumberUtil.formatWithComma(it.payoutAmount)}"

                // ✅ 테이블 데이터 변환
                val tableData = it.depositHistory.map { history ->
                    BaseTableRowModel(
                        listOf(
                            history.transactionDate,  // 날짜
                            history.transactionType,  // 거래내역
                            "${NumberUtil.formatWithComma(history.amount)}",  // 금액
                            "${history.interestRate}%",  // 이자율
                            "${NumberUtil.formatWithComma(history.balance)}"  // 잔액
                        )
                    )
                }

                // ✅ 테이블 데이터 업데이트
                tableAdapter.updateData(tableData)
            }
        }
        dialogBinding.btnNo.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun observeViewModel() {
        viewModel.savingsList.observe(viewLifecycleOwner) { savingsList ->
            this.savingsList = savingsList // ✅ 리스트 업데이트
            updateTable(savingsList)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.bankAccountCount.observe(viewLifecycleOwner) { response ->
            response?.let {
                binding.textStockCnt.text = "${it.depositProductCount} 개"
                binding.textDeposit2Cnt.text = "${it.savingsProductCount} 개"
            }
        }
    }

    private fun updateTable(savingsList: List<BankManageDto>) {
        val tableData = savingsList.mapIndexed { index, item ->
            BaseTableRowModel(
                listOf(
                    (index + 1).toString(),  // No.
                    "상품 ${item.savingsProductId}", // 상품명 (예제)
                    item.status, // 상품 유형 (status 활용)
                    "${item.durationWeeks}주", // 가입 기간
                    "${item.interestRate}%", // 이자율
                    "${item.depositAmount}원", // 납입금액
                    item.startDate, // 가입일
                    item.endDate, // 만기일
                    "${item.payoutAmount}원" // 예상 지급액
                )
            )
        }

        // ✅ 예상 지급액 총합 계산
        val totalPayout = savingsList.sumOf { it.payoutAmount }

        // ✅ UI 바인딩
        binding.textPayoutAmount2.text = NumberUtil.formatWithComma(totalPayout)

        tableAdapter.updateData(tableData)
    }
}

