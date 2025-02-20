package com.ladysparks.ttaenggrang.ui.revenue

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.size.ViewSizeResolver
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.databinding.FragmentRevenueStudentBinding
import kotlinx.coroutines.launch
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.DialogTaxStudentNationBinding
import com.ladysparks.ttaenggrang.ui.component.BaseTwoButtonDialog
import com.ladysparks.ttaenggrang.ui.component.DatePickerDialogHelper
import com.ladysparks.ttaenggrang.util.NumberUtil
import com.ladysparks.ttaenggrang.util.NumberUtil.formatWithComma
import com.ladysparks.ttaenggrang.util.showToast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class RevenueStudentFragment : BaseFragment<FragmentRevenueStudentBinding>(FragmentRevenueStudentBinding::bind, R.layout.fragment_revenue_student) {

    private lateinit var revenueViewModel: RevenueViewModel
    private lateinit var revenueStudentMyHistoryAdapter: RevenueStudentMyHistoryAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        revenueViewModel = ViewModelProvider(this).get(RevenueViewModel::class.java)

        revenueViewModel.fetchTaxStudentAmount()
        revenueViewModel.fetchStudentTaxHistory(null, null, null)
        revenueViewModel.getStudentBasicInfo()

        observeLiveData()

        revenueViewModel.fetchNationTaxHistory()
        revenueViewModel.getNationalTreasury()


        binding.btnShowNationTax.setOnClickListener{
            showNationTaxDialog()
        }

        binding.btnStduentPayUnpiadTax.setOnClickListener{
            showSuccessUnpaidDialog()
        }
        // 리사이클러 어뎁터 초기화 설명
        revenueStudentMyHistoryAdapter = RevenueStudentMyHistoryAdapter(emptyList())
        binding.recyclerStudnetThisMonthTax.apply {
            adapter = revenueStudentMyHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        revenueViewModel.studentTaxHistory.observe(viewLifecycleOwner) {
        }

        binding.bannerImage.load(R.drawable.logo9) {
            size(ViewSizeResolver(binding.bannerImage))
            crossfade(true)
        }
    }

    private fun observeLiveData() {
        revenueViewModel.studentBasicInfo.observe(viewLifecycleOwner) { response ->
            binding.textTaxStudentJobPayment.text = "${response?.jobName}(${formatWithComma(response?.baseSalary?:0)})"
        }

        revenueViewModel.nationTreasury.observe(viewLifecycleOwner) { response ->
            binding.textStudentShowNationTaxAmount.text = formatWithComma(response?.nationalTreasury ?:0)
        }

        revenueViewModel.studentTotalTaxInfo.observe(viewLifecycleOwner) { response ->
            binding.textTaxStudentName.text = response.studentName
            binding.textStudentTaxAmount.text = formatWithComma(response.totalAmount)

        }

        revenueViewModel.studentTaxHistory.observe(viewLifecycleOwner) { response ->
            val studentTaxHistory = response?: emptyList()
            revenueStudentMyHistoryAdapter.updateData(studentTaxHistory)
        }

        // 날짜 관련 초기 설정
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        var startDate = format.format(calendar.time)

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        var endDate = format.format(calendar.time)

        // UI 초기화 및 데이터 로드
        binding.textTaxHistoryStudentDateStart.setText(startDate)
        binding.textTaxHistoryStudentDateEnd.setText(endDate)
        revenueViewModel.fetchStudentTaxHistory(null, startDate, endDate)

        binding.textTaxHistoryStudentDateStart.setOnClickListener {
            DatePickerDialogHelper.showDatePickerDialog(requireContext(), isStartTime = true) { selectedDate ->
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(selectedDate) ?: selectedDate)

                val startDateParsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(formattedDate)
                val endDateParsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(endDate)

                if (startDateParsed != null && endDateParsed != null && startDateParsed.after(endDateParsed)) {
                    showToast("시작 날짜는 종료 날짜보다 나중일 수 없습니다")
                    return@showDatePickerDialog
                }



                startDate = formattedDate
                binding.textTaxHistoryStudentDateStart.setText(formattedDate)
                Log.d("TAG", "showNewVoteRegister: 값 테스트 $startDate")
                revenueStudentMyHistoryAdapter.updateData(emptyList())
                revenueViewModel.updateSelectedDates(formattedDate, endDate, null)
//                Log.d("testtest", "$formattedDate $endDate")
            }
        }

        binding.textTaxHistoryStudentDateEnd.setOnClickListener {
            DatePickerDialogHelper.showDatePickerDialog(requireContext(), isStartTime = false) { selectedDate ->
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(selectedDate) ?: selectedDate
                )
                // 종료 날짜는 시작 날짜보다 빠를 수 없음
                val endDateParsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(formattedDate)
                val startDateParsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(startDate)

                if (endDateParsed != null && startDateParsed != null && endDateParsed.before(startDateParsed)) {
                    showToast("종료 날짜는 시작날짜보다 빠를 수 없습니다")
                    return@showDatePickerDialog
                }

                endDate = formattedDate // 변수에 값 저장
                binding.textTaxHistoryStudentDateEnd.setText(formattedDate) // EditText에 표시
                Log.d("TAG", "showNewVoteRegister: 값 테스트 $endDate")
                revenueStudentMyHistoryAdapter.updateData(emptyList())
                revenueViewModel.updateSelectedDates(startDate, formattedDate, null)
            }
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