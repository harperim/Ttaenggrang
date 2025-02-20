package com.ladysparks.ttaenggrang.ui.bank

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.app.viewmodel.BankViewModel
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.data.model.dto.BankHistoryDto
import com.ladysparks.ttaenggrang.data.model.dto.BankItemDto
import com.ladysparks.ttaenggrang.data.model.dto.BankManageDto
import com.ladysparks.ttaenggrang.data.model.dto.ProductItemDto
import com.ladysparks.ttaenggrang.databinding.DialogAccountDetailBinding
import com.ladysparks.ttaenggrang.databinding.DialogBankProductDetailBinding
import com.ladysparks.ttaenggrang.databinding.FragmentBankStudentBinding
import com.ladysparks.ttaenggrang.ui.component.PieChartComponent
import com.ladysparks.ttaenggrang.ui.component.PieChartComponent2
import com.ladysparks.ttaenggrang.util.NumberUtil


class BankStudentFragment : BaseFragment<FragmentBankStudentBinding>(
    FragmentBankStudentBinding::bind,
    R.layout.fragment_bank_student
) {

    private val viewModel: BankViewModel by viewModels()
    private lateinit var bankAdapter: BankAdapter
    private lateinit var myAccountAdapter: BankMyAccountAdapter
    private var savingsList: List<Pair<BankManageDto, BankHistoryDto?>> = emptyList()
    private lateinit var pieChartComponent: PieChartComponent2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ PieChartComponent2 초기화
        val pieChart = binding.chartBank ?: return// ✅ XML에서 PieChart 가져오기
        pieChartComponent = PieChartComponent2(requireContext(), pieChart)


        //초기화
        initAdapter()
        observeViewModel()

        //서버에서 데이터 가져오기
        viewModel.fetchBankItems()
        viewModel.fetchUserSavings()
        viewModel.fetchAllBankAccounts()
        viewModel.calculateActiveDepositTotal()

        binding.btnAccountManage.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BankManageStudentFragment())
                .addToBackStack(null)
                .commit()
        }
    }


    private fun initAdapter() {
        bankAdapter = BankAdapter(emptyList()) { selectedItem ->
            Toast.makeText(requireContext(), "선택한 상품: ${selectedItem.name}", Toast.LENGTH_SHORT)
                .show()
            showItemDialog(selectedItem)
        }
        myAccountAdapter = BankMyAccountAdapter(emptyList()) { selectedItem ->
            Toast.makeText(
                requireContext(),
                "선택한 상품: ${selectedItem.second?.savingsName}",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        binding.recyclerBankProduct.apply {
            adapter = bankAdapter
        }
        binding.recyclerMyAccount.apply {
            adapter = myAccountAdapter
        }
    }

    // 은행 상품 상세 다이얼로그
    private fun showItemDialog(selectedItem: ProductItemDto) {
        viewModel.fetchBankItems()

        val dialogBinding = DialogBankProductDetailBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.4).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.textDialogTitle.text = selectedItem.name
        dialogBinding.textDuration.text = "${selectedItem.durationWeeks}주"
        dialogBinding.interestRate.text = "${selectedItem.interestRate}%"
        dialogBinding.textAmount.text = "${NumberUtil.formatWithComma(selectedItem.amount)}"
        dialogBinding.textContent.text = "중도해지 이자율 : ${selectedItem.earlyInterestRate}%"

        dialogBinding.btnYes.setOnClickListener {
//            val selectedProductId = selectedItem.  // 선택한 상품 ID
            val selectedDay = "MONDAY"  // 예제: "월요일" 가입
            val selectedId = selectedItem.id

            viewModel.subscribeToSavings( selectedId, selectedDay,)

            dialog.dismiss()
        }

        dialogBinding.btnNo.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()

    }

    private fun observeViewModel() {
        viewModel.bankItemList.observe(viewLifecycleOwner) { bankItems ->
            bankItems?.let {
                bankAdapter.updateData(it)
            }
        }
        viewModel.bankAccountList.observe(viewLifecycleOwner) { bankAccounts ->
            bankAccounts?.let {
                myAccountAdapter.updateData(it)
            }
        }

        viewModel.subscriptionResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                Toast.makeText(requireContext(), "적금 가입 성공!", Toast.LENGTH_SHORT).show()
                viewModel.fetchAllBankAccounts() // 가입된 계좌 목록 새로고침
            }
        }

        viewModel.bankAccountList.observe(viewLifecycleOwner) { savingsList ->
            savingsList?.let {
                myAccountAdapter.updateData(it) // 데이터 변경 시 UI 갱신
                this.savingsList = it
                viewModel.calculateActiveDepositTotal() // 계좌 목록 업데이트될 때마다 재계산
            }
        }

        viewModel.activeDepositTotal.observe(viewLifecycleOwner) { totalAmount ->
            binding.textContent.text = NumberUtil.formatWithComma(totalAmount)
        }

        // 차트 데이터 분모: 총 납입금액, 분자: 상위 2개
//        viewModel.bankAccountList.observe(viewLifecycleOwner) { bankAccounts ->
//            bankAccounts?.let {
//                myAccountAdapter.updateData(it)
//
//                // ✅ List<Pair<BankManageDto, BankHistoryDto?>>에서 첫 번째 값(BankManageDto)만 추출
//                val onlyBankManageList = it.map { pair -> pair.first }
//
//                // ✅ ViewModel에서 데이터 가공 후 차트 업데이트
//                val chartData = viewModel.getTopSavingsForChart(onlyBankManageList)
//
//                // ✅ 색상 리스트 (최대 3개)
//                val colorList = listOf(
//                    R.color.chartBlue,  // 1위 계좌 색상
//                    R.color.chartOrange, // 2위 계좌 색상
//                    R.color.chartGreen  // 기타 계좌 색상
//                )
//
//                // ✅ 차트 적용
//                pieChartComponent.setupPieChart(chartData, colorList)
//            }
//        }

        viewModel.bankAccountList.observe(viewLifecycleOwner) { bankAccounts ->
            Log.d("PieChartDebug", "🔹 bankAccountList 업데이트됨: $bankAccounts")

            bankAccounts?.let {
                myAccountAdapter.updateData(it) // ✅ UI 업데이트
                Log.d("PieChartDebug", "✅ 계좌 목록 UI 업데이트 완료")

                // ✅ BankManageDto만 추출
                val onlyBankManageList = it.map { pair -> pair.first }
                Log.d("PieChartDebug", "✅ BankManageDto 리스트 변환 완료: $onlyBankManageList")

                // ✅ ViewModel에서 차트에 사용할 데이터 가공
                val chartData = viewModel.getTopSavingsForChart(onlyBankManageList)
                Log.d("PieChartDebug", "✅ 변환된 차트 데이터: $chartData")

                // ✅ 색상 리스트 (최대 3개)
                val colorList = listOf(
                    R.color.chartBlue,  // 1위 계좌 색상
                    R.color.chartOrange, // 2위 계좌 색상
                    R.color.chartGreen  // 기타 계좌 색상
                )

                // ✅ 차트 데이터 적용
                if (chartData.isNotEmpty()) {
                    pieChartComponent.setupPieChart(chartData, colorList)
                    Log.d("PieChartDebug", "✅ 차트 업데이트 완료!")
                } else {
                    Log.e("PieChartDebug", "🚨 차트 데이터가 비어 있음!")
                }
            }
        }


        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }


    }
}


