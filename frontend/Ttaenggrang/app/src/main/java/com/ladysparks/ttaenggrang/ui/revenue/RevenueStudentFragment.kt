package com.ladysparks.ttaenggrang.ui.revenue

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.databinding.FragmentRevenueStudentBinding
import kotlinx.coroutines.launch
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.DialogTaxStudentNationBinding
import com.ladysparks.ttaenggrang.ui.component.BaseTwoButtonDialog
import com.ladysparks.ttaenggrang.util.NumberUtil
import com.ladysparks.ttaenggrang.util.NumberUtil.formatWithComma
import com.ladysparks.ttaenggrang.util.showToast


class RevenueStudentFragment : BaseFragment<FragmentRevenueStudentBinding>(FragmentRevenueStudentBinding::bind, R.layout.fragment_revenue_student) {

    private lateinit var revenueViewModel: RevenueViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        revenueViewModel = ViewModelProvider(this).get(RevenueViewModel::class.java)

        revenueViewModel.fetchTaxStudentAmount()
        observeLiveData()

        revenueViewModel.fetchNationTaxHistory()


        binding.btnShowNationTax.setOnClickListener{
            showNationTaxDialog()
        }

        binding.btnStduentPayUnpiadTax.setOnClickListener{
            showSuccessUnpaidDialog()
        }

//        getStudentPayments()
    }

    private fun observeLiveData() {

        revenueViewModel.studentTotalTaxInfo.observe(viewLifecycleOwner) { response ->
            binding.textTaxStudentName.text = response.studentName
            binding.textStudentTaxAmount.text = formatWithComma(response.totalAmount)

        }
    }


    private fun showNationTaxDialog() {
        val dialogBinding = DialogTaxStudentNationBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialog.show()

        val revenueNationHistoryAdapter = RevenueNationHistoryAdapter(emptyList())
        dialogBinding.recyclerNationTax.apply {
            adapter = revenueNationHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        //  LiveData 옵저버 추가 (데이터 변경될 때 자동으로 UI 갱신)
        revenueViewModel.nationTaxHistory.observe(viewLifecycleOwner) { nationTaxHistory ->
            val historyList = nationTaxHistory ?: emptyList()
            revenueNationHistoryAdapter.updateData(historyList)

            if (historyList.isNotEmpty()) {
                dialogBinding.textNullNationTax.visibility = View.GONE
                dialogBinding.recyclerNationTax.visibility = View.VISIBLE
            } else {
                dialogBinding.textNullNationTax.visibility = View.VISIBLE
                dialogBinding.recyclerNationTax.visibility = View.GONE
            }
        }

        //  데이터를 가져오기 전에 다이얼로그가 열리므로, 최신 데이터 가져오기
        revenueViewModel.fetchNationTaxHistory()

        dialogBinding.btnClose.setOnClickListener{ dialog.dismiss() }
        dialogBinding.btnCloseStudentNationTax.setOnClickListener{ dialog.dismiss() }

    }






//    private fun getStudentPayments() {
//        lifecycleScope.launch {
//            runCatching {
//                RetrofitUtil.taxService.getStudentTaxPayments()
//            }.onSuccess {
//                val payments = it.data ?: emptyList()
//                if (payments.isNotEmpty()) {
//                    binding.recyclerStudnetThisMonthTax.visibility = View.VISIBLE
//                    binding.textNullNationTax.visibility = View.GONE
//                    payments.forEach {
//                        Log.d("test", "tax ID: ${it.taxId}, amount: ${it.amount}, status: ${it.status}")
//                    }
//                } else {
//                    binding.textNullNationTax.visibility = View.VISIBLE
//                    binding.recyclerStudnetThisMonthTax.visibility = View.GONE
//                    Log.d("test", "No tax history")
//                }
//            }.onFailure {
//                Log.d("test", "api failure")
//            }
//        }
//    }

    private fun showSuccessUnpaidDialog() {
        revenueViewModel.getOverDueTax()  // ✅ 최신 데이터 가져오기

        revenueViewModel.overdueTotal.observe(viewLifecycleOwner) { overdueTax ->
            overdueTax?.let {
                if (it.totalAmount == 0) {
                    showToast("미납한 세금이 없습니다")
                } else {
                    createBuySuccessDialog().show()
                }
            } ?: showToast("미납 세금 정보를 가져올 수 없습니다")
        }

    }

    // 구매 성공 다이얼로그 생성 함수
    private fun createBuySuccessDialog(): BaseTwoButtonDialog {
        return BaseTwoButtonDialog(
            context = requireContext(),
            title = "미납 세금 납부 완료",
            message = "미납 세금을 모두 납부했습니다",
            positiveButtonText = "확인",
            statusImageResId = R.drawable.ic_alert_possible,
            showCloseButton = true,
            onPositiveClick = null,
            onNegativeClick = null,
            onCloseClick = null
        )
    }


}