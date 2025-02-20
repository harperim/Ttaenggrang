package com.ladysparks.ttaenggrang.ui.revenue

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Visibility
import coil.load
import coil.size.ViewSizeResolver
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.data.model.dto.TaxDto
import com.ladysparks.ttaenggrang.data.model.response.TaxStudentHistoryResponse
import com.ladysparks.ttaenggrang.data.model.response.TaxStudentTotalResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.DialogTaxStudentNationBinding
import com.ladysparks.ttaenggrang.databinding.DialogTaxTeacherAddBinding
import com.ladysparks.ttaenggrang.databinding.DialogTaxTeacherStudentBinding
import com.ladysparks.ttaenggrang.databinding.DialogTaxTeacherUseBinding
import com.ladysparks.ttaenggrang.databinding.FragmentRevenueTeacherBinding
import com.ladysparks.ttaenggrang.ui.component.BaseDatePickerRange
import com.ladysparks.ttaenggrang.ui.component.DatePickerDialogHelper
import com.ladysparks.ttaenggrang.util.NumberUtil.formatWithComma
import com.ladysparks.ttaenggrang.util.showToast
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar
import java.util.Locale

class RevenueTeacherFragment : BaseFragment<FragmentRevenueTeacherBinding>(FragmentRevenueTeacherBinding::bind, R.layout.fragment_revenue_teacher) {
    private lateinit var revenueViewModel: RevenueViewModel
    private lateinit var taxInfoAdapter: RevenueTeacherInfoAdapter
    private lateinit var studentTaxAdapter: RevenueTeacherStudentAdapter
    private lateinit var revenueTeacherNationHistoryAdapter: RevenueTeacherNationHistoryAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        revenueViewModel = ViewModelProvider(this).get(RevenueViewModel::class.java)
        taxInfoAdapter = RevenueTeacherInfoAdapter(emptyList())

        binding.recyclerTaxInfo.apply {
            adapter = taxInfoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        studentTaxAdapter = RevenueTeacherStudentAdapter(emptyList()) { studentId ->
            showStudentTaxDialog(studentId)
        }
        // 학생 세금 납부 내역 RecyclerView 설정
        binding.recyclerTeacherStudentTaxHistory.apply {
            adapter = studentTaxAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        revenueTeacherNationHistoryAdapter = RevenueTeacherNationHistoryAdapter(emptyList())

        binding.recyclerTeacherNationTaxHistory.apply {
            adapter = revenueTeacherNationHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        revenueViewModel.getNationalTreasury()


        revenueViewModel.fetchTaxList()
        revenueViewModel.fetchTeacherStudentPaymentsAmount()
        revenueViewModel.fetchStudentTaxHistory(1, "2025-01-01", "2025-03-01")
        revenueViewModel.fetchNationTaxHistory()

        observeLiveData()

        // 세금 생성하기 다이얼로그
        binding.btnRegisterTaxInfo.setOnClickListener {
            showTaxRegistrationDialog()
        }

        // 국세 사용하기
        binding.btnUseNationTax.setOnClickListener{
            showUseTaxDialog()
        }

        // 국세 내역 보기
        binding.linearNationTaxHistoryTitle.setOnClickListener{
            showNationTaxDialog()
        }

        binding.bannerImage.load(R.drawable.logo9) {
            size(ViewSizeResolver(binding.bannerImage))
            crossfade(true)
        }
    }

    private fun observeLiveData(){
        revenueViewModel.taxList.observe(viewLifecycleOwner) { response ->

            val taxList = response ?: emptyList()
            taxInfoAdapter.updateData(taxList)

            if (taxList.isNotEmpty()) {
                binding.textNullTaxInfo.visibility = View.GONE
                binding.recyclerTaxInfo.visibility = View.VISIBLE
            } else{
                binding.textNullTaxInfo.visibility = View.VISIBLE
                binding.recyclerTaxInfo.visibility = View.GONE
            }
        }

        revenueViewModel.studentTaxAmountList.observe(viewLifecycleOwner) { response ->

            val studentTaxAmountList = response ?: emptyList()
            studentTaxAdapter.updateData(studentTaxAmountList)

            if (studentTaxAmountList.isNotEmpty()) {
                binding.textNullTeacherStudentTaxHistory.visibility = View.GONE
                binding.recyclerTeacherStudentTaxHistory.visibility = View.VISIBLE
            } else{
                binding.textNullTeacherStudentTaxHistory.visibility = View.VISIBLE
                binding.recyclerTeacherStudentTaxHistory.visibility = View.GONE
            }

        }

        revenueViewModel.nationTaxHistory.observe(viewLifecycleOwner) { response ->
            val nationTaxHistory = response ?: emptyList()
            revenueTeacherNationHistoryAdapter.updateData(nationTaxHistory)

            if (nationTaxHistory.isNotEmpty()) {
                binding.textNullNationTaxHistory.visibility = View.GONE
                binding.recyclerTeacherNationTaxHistory.visibility = View.VISIBLE
            } else{
                binding.textNullNationTaxHistory.visibility = View.VISIBLE
                binding.recyclerTeacherNationTaxHistory.visibility = View.GONE
            }
        }

        revenueViewModel.nationTreasury.observe(viewLifecycleOwner) { response ->
            binding.textStudentShowNationTaxAmount.text = formatWithComma(response?.nationalTreasury ?:0)
        }
    }

    private fun showTaxRegistrationDialog() {
        val dialogBinding = DialogTaxTeacherAddBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnStudentRegistration.setOnClickListener {
            val taxName = dialogBinding.editAddTaxName.text.toString().trim()
            val taxRateInput = dialogBinding.editAddTaxRate.text.toString().trim()
            val taxDescription = dialogBinding.editAddTaxDescription.text.toString().trim()

            if (taxName.isEmpty() || taxRateInput.isEmpty() || taxDescription.isEmpty()) {
                showToast("모든 항목을 입력해야 합니다")
                return@setOnClickListener
            }

            val taxRate = taxRateInput.toDoubleOrNull()

            if (taxRate == null || taxRate <=  0 || taxRate >= 1) {
                showToast("세율은 0 초과 1 미만의 소수를 입력 해야 합니다")
                return@setOnClickListener
            }

            revenueViewModel.registerTax(TaxDto(taxName= taxName, taxRate = taxRate, taxDescription = taxDescription), {
                dialog.dismiss()
                showToast("세금이 성공적으로 등록되었습니다")
                revenueViewModel.fetchTaxList()
            }, {
                showToast("세금 등록에 실패하였습니다")
            })
        }

        dialog.show()
    }

    private var studentTaxObserver: androidx.lifecycle.Observer<List<TaxStudentHistoryResponse>>? = null

    private fun showStudentTaxDialog(studentId: Int) {
        val dialogBinding = DialogTaxTeacherStudentBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // ✅ 학생의 총 납부 금액과 이름 불러오기
        revenueViewModel.fetchTaxStudentAmount(studentId)
        revenueViewModel.getTeacherStudentBasicInfo(studentId)

        // 어댑터 초기화 (재사용)
        val studentTaxHistoryAdapter = StudentTaxHistoryAdapter(emptyList())
        dialogBinding.recyclerStudentTaxPayHistory.apply {
            adapter = studentTaxHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        revenueViewModel.startDate.observe(viewLifecycleOwner) { date ->
            dialogBinding.textTaxHistoryStudentDateStart.setText(date)
        }

        revenueViewModel.endDate.observe(viewLifecycleOwner) { date ->
            dialogBinding.textTaxHistoryStudentDateEnd.setText(date)
        }

        revenueViewModel.teacherStudentBasicInfo.observe(viewLifecycleOwner) { response ->
            dialogBinding.textTaxHistoryStudentJobPayment.text = "${response?.jobName}(${formatWithComma(response?.baseSalary ?: 0)})"
        }

        // 기존 옵저버 제거
        studentTaxObserver?.let {
            revenueViewModel.studentTaxHistory.removeObserver(it)
        }

        // 새로운 옵저버 생성 및 등록
        studentTaxObserver = androidx.lifecycle.Observer { taxHistoryList ->
            if (!taxHistoryList.isNullOrEmpty()) {
                studentTaxHistoryAdapter.updateData(taxHistoryList)
                dialogBinding.textNullStudentTaxPayHistory.visibility = View.GONE
                dialogBinding.recyclerStudentTaxPayHistory.visibility = View.VISIBLE
            } else {
                dialogBinding.textNullStudentTaxPayHistory.visibility = View.VISIBLE
                dialogBinding.recyclerStudentTaxPayHistory.visibility = View.GONE
            }
            // 학생 다시 불러서 신상정보 출력해야 함
        }

        revenueViewModel.studentTaxHistory.observe(viewLifecycleOwner, studentTaxObserver!!)

        revenueViewModel.studentTotalTaxInfo.observe(viewLifecycleOwner) { studentInfo ->
            studentInfo?.let {
                dialogBinding.textTaxHistoryStudentName.text = it.studentName  // 🟢 학생 이름 표시
                dialogBinding.textStudentHistoryTaxAmount.text = formatWithComma(it.totalAmount)
            }
        }

        // 날짜 관련 초기 설정
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        var startDate = format.format(calendar.time)

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        var endDate = format.format(calendar.time)

        // UI 초기화 및 데이터 로드
        dialogBinding.textTaxHistoryStudentDateStart.setText(startDate)
        dialogBinding.textTaxHistoryStudentDateEnd.setText(endDate)
        revenueViewModel.fetchStudentTaxHistory(studentId, startDate, endDate)

        // 다이얼로그 표시
        dialog.show()


        dialogBinding.textTaxHistoryStudentDateStart.setOnClickListener {
            DatePickerDialogHelper.showDatePickerDialog(requireContext(), isStartTime = true) { selectedDate ->
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(selectedDate) ?: selectedDate)
                startDate = formattedDate

                val startDateParsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(formattedDate)
                val endDateParsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(endDate)

                if (startDateParsed != null && endDateParsed != null && startDateParsed.after(endDateParsed)) {
                    showToast("시작 날짜는 종료 날짜보다 나중일 수 없습니다")
                    return@showDatePickerDialog
                }

                dialogBinding.textTaxHistoryStudentDateStart.setText(formattedDate)
                studentTaxHistoryAdapter.updateData(emptyList())
                revenueViewModel.updateSelectedDates(formattedDate, endDate, studentId)
            }
        }

        dialogBinding.textTaxHistoryStudentDateEnd.setOnClickListener {
            DatePickerDialogHelper.showDatePickerDialog(requireContext(), isStartTime = false) { selectedDate ->
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(selectedDate) ?: selectedDate
                )
                endDate = formattedDate // 변수에 값 저장

                // 종료 날짜는 시작 날짜보다 빠를 수 없음
                val endDateParsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(formattedDate)
                val startDateParsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(startDate)

                if (endDateParsed != null && startDateParsed != null && endDateParsed.before(startDateParsed)) {
                    showToast("종료 날짜는 시작날짜보다 빠를 수 없습니다")
                    return@showDatePickerDialog
                }

                dialogBinding.textTaxHistoryStudentDateEnd.setText(formattedDate) // EditText에 표시
                studentTaxHistoryAdapter.updateData(emptyList())
                revenueViewModel.updateSelectedDates(startDate, formattedDate, studentId)
            }
        }

        revenueViewModel.fetchStudentTaxHistory(studentId, startDate, endDate)

        // 클린업
        dialog.setOnDismissListener {
            studentTaxObserver?.let {
                revenueViewModel.studentTaxHistory.removeObserver(it)
                studentTaxObserver = null
            }
            dialogBinding.recyclerStudentTaxPayHistory.adapter = null
        }

        dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnCloseStudenTaxHistory.setOnClickListener { dialog.dismiss() }
    }

    // 국세 사용 다이얼로그
    private fun showUseTaxDialog() {
        val dialogBinding = DialogTaxTeacherUseBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()



        dialogBinding.btnClose.setOnClickListener{ dialog.dismiss() }
        dialogBinding.btnUseNationTax.setOnClickListener{
            val name = dialogBinding.editUseTaxName.text.toString().trim()
            val amountInput = dialogBinding.editUseTaxAmount.text.toString().trim()
            val description = dialogBinding.editUseTaxDescription.text.toString().trim()


            if( name.isEmpty() || amountInput.isEmpty() || description.isEmpty()) {
                showToast("모든 항목을 작성해야 합니다")
                return@setOnClickListener
            }
            val amount = amountInput.toIntOrNull()

            if (amount == null || amount < 0) {
                showToast("금액은 1 이상의 자연수여야 합니다")
                return@setOnClickListener
            }

            revenueViewModel.useTax(name, amount, description, {
                dialog.dismiss()
                showToast("국세 사용에 성공했습니다")
                revenueViewModel.getNationalTreasury()
                revenueViewModel.fetchNationTaxHistory()
            }, {
                dialog.dismiss()
                showToast("국세 사용에 실패했습니다")
            })

        }
        dialog.show()
    }

    fun showNationTaxDialog() {
        Log.d("NationTaxRecycler", "2")
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
}