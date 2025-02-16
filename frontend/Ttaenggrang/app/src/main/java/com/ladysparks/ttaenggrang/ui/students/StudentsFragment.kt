package com.ladysparks.ttaenggrang.ui.students

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.base.BaseTableAdapter
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.ui.component.BaseTwoButtonDialog
import com.ladysparks.ttaenggrang.data.model.request.StudentSingleCreateRequest
import com.ladysparks.ttaenggrang.data.model.response.StockResponse
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.model.response.StudentSavingResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.DialogProductDetailBinding
import com.ladysparks.ttaenggrang.databinding.DialogStockDetailBinding
import com.ladysparks.ttaenggrang.databinding.DialogStudentRegistrationBinding
import com.ladysparks.ttaenggrang.databinding.FragmentStudentsBinding
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel
import com.ladysparks.ttaenggrang.ui.component.IncentiveDialogFragment
import com.ladysparks.ttaenggrang.ui.component.PieChartComponent
import com.ladysparks.ttaenggrang.util.NumberUtil
import com.ladysparks.ttaenggrang.util.showErrorDialog
import com.ladysparks.ttaenggrang.util.showToast
import kotlinx.coroutines.launch

class StudentsFragment : BaseFragment<FragmentStudentsBinding>(
    FragmentStudentsBinding::bind,
    R.layout.fragment_students
) {
    private lateinit var studentsViewModel: StudentsViewModel

    private var jobListCache: List<JobDto> = emptyList()
    private var studentListCache: List<StudentMultiCreateResponse> = emptyList()
    private var savingProductCache: List<StudentSavingResponse> = emptyList()


    // Recycleriew 를 표시하기 위한 Adapter : finance의경우 클릭이벤트가있어서 새로운 Adapter 사용
    private lateinit var studentAdapter: BaseTableAdapter
    private lateinit var financeAdapter: FinanceTableAdapter
    private lateinit var savingAdapter: BaseTableAdapter

    // 현재 홞성화된 탭을 저장하는 변수
    private var isStudentTab: Boolean = true
    private var selectStudentSavingSum: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        studentsViewModel = ViewModelProvider(this).get(StudentsViewModel::class.java)

        initAdapter()
        initObserver()
        initEvent()

        // Data 요청
        studentsViewModel.fetchStudentList()
        studentsViewModel.fetchJobList()
    } // ....onViewCreated

    private fun initAdapter() {
        // Tap: 학생정보
        val studentHeader = listOf("고유번호", "이름", "직업 (월급)", "아이디", "비밀번호")
        studentAdapter = BaseTableAdapter(studentHeader, emptyList()) { rowIndex, rowData ->
            showStudentInfoDialog(rowData)
        }
        binding.recyclerStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerStudents.adapter = studentAdapter

        // Tap: 재정 상태
        val financeHeader = listOf("고유번호", "이름", "계좌 잔액", "은행 가입 상품", "보유 주식")
        financeAdapter = FinanceTableAdapter(financeHeader, emptyList(),
            onProductClick = { _, rowData ->
                val userId = rowData[0] // ✅ 사용자 ID 가져오기
                showProductPopup(userId, rowData)
            },
            onStockClick = { _, rowData ->
                val userId = rowData[0] // ✅ 사용자 ID 가져오기
                showStockPopup(userId, rowData)
            }
        )
        binding.recyclerFinance.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFinance.adapter = financeAdapter


        // 주식 savingAdapter
    }

    private fun showStudentInfoDialog(rowData: List<String>) {
        Log.d("TAG", "showStudentInfoDialog: ${jobListCache}")
        val bottomSheet = StudentInfoDialog.newInstance(rowData, jobListCache)
        bottomSheet.show(childFragmentManager, "StudentInfoBottomSheet")
    }
    private var pendingUserId: Int? = null

    private fun initObserver() {
        // Error
        studentsViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showErrorDialog(Throwable(it))
                studentsViewModel.clearErrorMessage()
            }
        }

        // 학생 리스트
        studentsViewModel.studentList.observe(viewLifecycleOwner) { studentList ->
            updateRecyclerView(studentList)
        }

        // Other Response
        studentsViewModel.studentList.observe(viewLifecycleOwner) { studentList ->
            studentListCache = studentList!!
        }

        // 직업리스트
        studentsViewModel.jobList.observe(viewLifecycleOwner) { jobList ->
            jobListCache = jobList // ✅ LiveData 변경 시 전역 변수 업데이트
        }

        studentsViewModel.weeklyPaymentStatus.observe(viewLifecycleOwner) { response ->
            showToast("주급이 지급되었습니다")
        }

        studentsViewModel.bonusPaymentStatus.observe(viewLifecycleOwner) { response ->
            showToast("인센티브가 지급되었습니다")
        }

        studentsViewModel.savingList.observe(viewLifecycleOwner) { response ->
            selectStudentSavingSum = response.sumOf { it.totalAmount }
            savingProductCache = response
            updateSavingRcyclerView(response)
        }

        studentsViewModel.stockList.observe(viewLifecycleOwner) { response ->
            selectStudentSavingSum = response.sumOf { it.currentTotalPrice.toDouble() }

        }
    }

    private fun updateRecyclerView(studentList: List<StudentMultiCreateResponse>?) {
        if (studentList.isNullOrEmpty()) return

        if (isStudentTab) {
            val formattedData = studentList!!.mapIndexed { index, student ->
                Log.d("TTT", "${student.jobInfo?.jobName}")
                BaseTableRowModel(
                    listOf(
//                        (index + 1).toString(),
                        student.id.toString(),
                        student.name ?: "N/A",
                        "${student.jobInfo?.jobName ?: "시민"} (${NumberUtil.formatWithComma(student.jobInfo?.baseSalary ?: 0)})",
                        student.username,
                        student.teacher.password
                    )
                )
            }
            studentAdapter.updateData(formattedData)
            studentAdapter.notifyDataSetChanged()
        } else {
            binding.recyclerStudents.visibility = View.GONE
            binding.recyclerFinance.visibility = View.VISIBLE

            val formattedData = studentList!!.mapIndexed { index, student ->
                BaseTableRowModel(
                    listOf(
                        student.id.toString(),
//                        (index + 1).toString(),
                        student.name ?: "N/A",
                        NumberUtil.formatWithComma(student.bankAccount?.balance ?: 0),
                        "자세히",
                        "자세히",
                    )
                )
            }
            financeAdapter.updateData(formattedData)
            financeAdapter.notifyDataSetChanged()
        }
    }

    private fun updateSavingRcyclerView(studentList: List<StudentSavingResponse>?) {
        val formattedData = studentList!!.mapIndexed { index, savingItem ->
            BaseTableRowModel(
                listOf(
                    savingItem.subscriptionDate,
                    savingItem.savingsName,
                    NumberUtil.formatWithComma(savingItem.amount),
                    "${savingItem.interest}%",
                    NumberUtil.formatWithComma(savingItem.totalAmount)
                )
            )
        }

        savingAdapter.updateData(formattedData)
        savingAdapter.notifyDataSetChanged()
    }


    private fun initEvent() {
        binding.btnTabStudentInfo.setOnClickListener {
            selectTab(true)
            updateRecyclerView(studentsViewModel.studentList.value)
        }

        binding.btnTabFincialStatus.setOnClickListener {
            selectTab(false)
            updateRecyclerView(studentsViewModel.studentList.value)
        }

        // 주급 버튼
        binding.btnSalary.setOnClickListener {
            val dialog = BaseTwoButtonDialog(
                context = requireContext(),
                statusImageResId = R.drawable.ic_warning,
                title = "주급 지급",
                message = "모든 학생에게 주급을 지급 합니다",
                showCloseButton = false,
                negativeButtonText = "취소",
                onNegativeClick = { },
                positiveButtonText = "지급",
                onPositiveClick = {
                    studentsViewModel.processStudentWeeklySalary()
                }
            )
            dialog.show()
        }

        // 인센티브 버튼
        binding.btnIncentive.setOnClickListener {
            showIncentiveDialog()
        }

        // 학생 등록 버튼
        binding.btnCreateStudent.setOnClickListener {
            val dialog = BaseTwoButtonDialog(
                context = requireContext(),
                title = "학생 정보를 추가합니다",
                message = "",
                positiveButtonText = "여러 학생 추가",
                negativeButtonText = "신규 학생 추가",
                statusImageResId = R.drawable.ic_vote,
                showCloseButton = false,
                onPositiveClick = {
                    // 🔹 1단계 다이얼로그 표시
//                    val viewModel = ViewModelProvider(requireActivity())[StudentViewModel::class.java]
                    val stepOneDialog = StudentStepOneDialogFragment(studentsViewModel)
                    stepOneDialog.show(parentFragmentManager, "StepOneDialog")
                },
                onNegativeClick = {
                    showSingleStudentAddDialog()
                }
            )

            // 다이얼로그 표시
            dialog.show()
        }

    }

    private fun showIncentiveDialog() {
        val studentMap =
            studentListCache.associateBy({ it.name ?: "이름 없음" }, { it.id ?: -1 }) // 학생 데이터
        val dialog = IncentiveDialogFragment.newInstance(studentMap)

        dialog.setOnConfirmListener { studentId, price ->
            studentsViewModel.processStudentBonus(studentId, price) // ✅ API 호출
        }

        dialog.show(parentFragmentManager, "IncentiveDialog")
    }


    private fun showSingleStudentAddDialog() {
        val dialogBinding =
            DialogStudentRegistrationBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // job 설정
        val jobList = jobListCache.map { it.jobName }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, jobList)
        dialogBinding.editJob.adapter = adapter

        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnStudentRegistration.setOnClickListener {
            // 유효성 검사
            val name = dialogBinding.editAddName.text
            val id = dialogBinding.editId.text
            val password = dialogBinding.editPassword.text
            if (name.isNullOrEmpty() || id.isNullOrEmpty() || password.isNullOrEmpty()) {
                showToast("모든 항목을 빠짐없이 입력해주세요")
                return@setOnClickListener
            }

            // 단순 데이터 추가
            val user = StudentSingleCreateRequest(
                username = id.toString(),
                name = name.toString(),
                password = password.toString(),
                profileImage = ""
            )
            lifecycleScope.launch {
                runCatching {
                    RetrofitUtil.teacherService.singleCreate(user)
                }.onSuccess { data ->
                    showToast("회원추가 완료 !")
                    studentsViewModel.fetchStudentList()
                }.onFailure { exception ->
                    showErrorDialog(exception)
                }
            }

            dialog.dismiss()  // 다이얼로그 닫기
        }

        dialog.show()
    }

    //    recyclerStudents


    // 은행 가입 상품 조회 팝업
    private fun showProductPopup(userId: String, data: List<String>) {
        studentsViewModel.userSavingSubscriptions(userId.toInt())

        val dialogBinding = DialogProductDetailBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()
        // Adapter 설정
        val savingListHeader = listOf("가입날자", "상품명", "월 납입액", "이자율", "잔액")
        savingAdapter = BaseTableAdapter(savingListHeader, emptyList())
        dialogBinding.recyclerSaving.layoutManager = LinearLayoutManager(requireContext())
        dialogBinding.recyclerSaving.adapter = savingAdapter
        dialogBinding.recyclerSaving.post {
            dialogBinding.textTotalSaving.text = NumberUtil.formatWithComma(selectStudentSavingSum)
        }

        dialogBinding.btnRegister.visibility = View.GONE
        dialogBinding.btnCancel.text = "확인"
        dialogBinding.imageButton.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show() // 다이얼로그 띄우기
    }


    private fun showStockPopup(userId: String, data: List<String>) {
        studentsViewModel.stockList(userId.toInt())

        val dialogBinding = DialogStockDetailBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // 어댑터 설정
        val stockAdapter = StockAdapter(emptyList())
        dialogBinding.recyclerStockList.layoutManager = LinearLayoutManager(requireContext())
        dialogBinding.recyclerStockList.adapter = stockAdapter

        //  LiveData 옵저버 추가 (주식 데이터 업데이트 시 UI 자동 반영)
        studentsViewModel.stockList.observe(viewLifecycleOwner) { stockResponse ->
            stockAdapter.updateData(stockResponse)
            dialogBinding.textTotalSaving.text = NumberUtil.formatWithComma(selectStudentSavingSum)
            updateChart(dialogBinding)
        }

        // UI 설정
        dialogBinding.btnCancel.visibility = View.GONE
        dialogBinding.btnRegister.text = "확인"
        dialogBinding.btnRegister.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun updateChart(dialogBinding: DialogStockDetailBinding) {
        val pieChartComponent = PieChartComponent(requireContext(), dialogBinding.pieChart)

        // 데이터 설정 (뷰모델 값 반영)
        val dataList = listOf(
            3.2f to "삼성",
            5.5f to "LG",
            8f to "JYP"
        )

        // 색상 설정
        val colorList = listOf(
            R.color.chartBlue,  // 계좌 잔액
            R.color.chartPink,  // 투자
            R.color.chartPurple // 저축
        )

        // 동적 데이터 전달하여 차트 업데이트
        pieChartComponent.setupPieChart(dataList, colorList)
    }



    // Tab 변경 이벤트
    private fun selectTab(isStudentInfo: Boolean) {
        val context = context ?: return
        isStudentTab = isStudentInfo  // ✅ 선택된 탭을 변수에 저장

        if (isStudentInfo) {
            // "학생 정보 탭" 활성화
            binding.tvStudentInfo.setTextAppearance(R.style.heading4)
            binding.tvStudentInfo.setTextColor(ContextCompat.getColor(context, R.color.mainOrange))
            binding.underlineStudentInfo.visibility = View.VISIBLE
            if (studentsViewModel.studentList.value.isNullOrEmpty()) {
                binding.textNullStudent.visibility = View.VISIBLE
                binding.recyclerStudents.visibility = View.GONE
                binding.recyclerFinance.visibility = View.GONE
            } else {
                binding.textNullStudent.visibility = View.GONE
                binding.recyclerFinance.visibility = View.GONE
                binding.recyclerStudents.visibility = View.VISIBLE
            }

            // "재정 상태" 비활성화
            binding.tvFinancialStatus.setTextAppearance(R.style.body2)
            binding.tvFinancialStatus.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.lightGray
                )
            )
            binding.underlineFinancialStatus.visibility = View.GONE
        } else {
            // 학생 정보 비활성화
            binding.tvStudentInfo.setTextAppearance(R.style.body2)
            binding.tvStudentInfo.setTextColor(ContextCompat.getColor(context, R.color.lightGray))
            binding.underlineStudentInfo.visibility = View.GONE

            if (studentsViewModel.studentList.value.isNullOrEmpty()) {
                binding.textNullStudent.visibility = View.VISIBLE
                binding.recyclerStudents.visibility = View.GONE
                binding.recyclerFinance.visibility = View.GONE
            } else {
                binding.textNullStudent.visibility = View.GONE
                binding.recyclerStudents.visibility = View.GONE
                binding.recyclerFinance.visibility = View.VISIBLE
            }

            // 재정 상태 활성화
            binding.tvFinancialStatus.setTextAppearance(R.style.heading4)
            binding.tvFinancialStatus.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.mainOrange
                )
            )
            binding.underlineFinancialStatus.visibility = View.VISIBLE
        }
    }


}
