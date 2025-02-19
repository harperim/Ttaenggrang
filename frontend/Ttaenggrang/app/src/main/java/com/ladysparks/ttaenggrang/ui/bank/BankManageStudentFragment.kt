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
import com.ladysparks.ttaenggrang.data.model.dto.BankHistoryDto
import com.ladysparks.ttaenggrang.data.model.dto.BankManageDto
import com.ladysparks.ttaenggrang.databinding.DialogAccountDetailBinding
import com.ladysparks.ttaenggrang.databinding.DialogSavingPayoutBinding
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
    private var savingsList: List<Pair<BankManageDto, BankHistoryDto?>> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //초기화
        initAdapter()
        observeViewModel()

        // 서버에서 데이터 가져오기
        viewModel.fetchUserSavings()
        viewModel.fetchUserSavingCount()
        viewModel.fetchAllBankAccounts()

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
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
                val selectedItem = savingsList[rowIndex].first
                val savingsId = selectedItem.savingsProductId
                val status = selectedItem.status // ✅ 상품 상태 가져오기

                showBankHistoryDialog(savingsId, status) // ✅ 상태값 전달
            }
        )
        binding.recyclerBankManageList.adapter = tableAdapter
    }

    private fun showBankHistoryDialog(savingsSubscriptionId: Int, status: String) {
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


              //  if (status == "MATURITY") {
                    dialogBinding.btnYes.visibility = View.VISIBLE
                    dialogBinding.btnYes.setOnClickListener {
                        viewModel.requestPayout(savingsSubscriptionId) // 만기 지급 요청 실행
                        showAlertDialog()
                        dialog.dismiss() // ✅ 다이얼로그 닫기
                    }
               // }

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

    private fun showAlertDialog() {
        val dialogBinding = DialogSavingPayoutBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.3).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.btnDialogConfirm.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun observeViewModel() {
//        viewModel.savingsList.observe(viewLifecycleOwner) { savingsList ->
//            this.savingsList = savingsList // ✅ 리스트 업데이트
//            updateTable(savingsList)
//        }

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

        viewModel.bankAccountList.observe(viewLifecycleOwner) { savingsList ->
            this.savingsList = savingsList
            updateTable(savingsList)
        }

        viewModel.payoutResult.observe(viewLifecycleOwner) { payoutResponse ->
            payoutResponse?.let {
                Toast.makeText(requireContext(), "적금 만기 지급 완료! 지급액: ${it.payoutAmount}", Toast.LENGTH_LONG).show()
                binding.textPayoutAmount.text = "지급 완료: ${it.payoutAmount}원"
            }
        }
    }

    private fun updateTable(savingsList: List<Pair<BankManageDto, BankHistoryDto?>>) {
        val tableData = savingsList.mapIndexed { index, item ->
            val bankManage = item.first
            val bankHistory = item.second

            BaseTableRowModel(
                listOf(
                    (index + 1).toString(),  // No.
                    bankHistory?.savingsName ?: "알 수 없음", // ✅ 상품명 (savingsName)
                    bankManage.status, // 상품 유형 (status 활용)
                    "${bankManage.durationWeeks}주", // 가입 기간
                    "${bankManage.interestRate.toInt()}%", // 이자율
                    NumberUtil.formatWithComma(bankManage.depositAmount), // 납입금액
                    bankManage.startDate, // 가입일
                    bankManage.endDate, // 만기일
                    NumberUtil.formatWithComma(bankManage.payoutAmount) // 예상 지급액
                )
            )
        }

        // 예상 지급액 총합 계산
        val totalPayout = savingsList.sumOf { it.first.payoutAmount }
        binding.textPayoutAmount2.text = NumberUtil.formatWithComma(totalPayout)

        tableAdapter.updateData(tableData)
    }
}

