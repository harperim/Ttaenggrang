package com.ladysparks.ttaenggrang.ui.students

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
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
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.DialogIncentiveBinding
import com.ladysparks.ttaenggrang.databinding.DialogStudentRegistrationBinding
import com.ladysparks.ttaenggrang.databinding.DialogVoteParticipationBinding
import com.ladysparks.ttaenggrang.databinding.FragmentStudentsBinding
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel
import com.ladysparks.ttaenggrang.ui.component.IncentiveDialogFragment
import com.ladysparks.ttaenggrang.util.showErrorDialog
import com.ladysparks.ttaenggrang.util.showToast
import kotlinx.coroutines.launch

class StudentsFragment : BaseFragment<FragmentStudentsBinding>(
    FragmentStudentsBinding::bind,
    R.layout.fragment_students
) {
    private lateinit var studentsViewModel: StudentsViewModel

    // 직업 리스트
    private var jobListCache: List<JobDto> = emptyList()
    private var studentListCache: List<StudentMultiCreateResponse> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // viewModel
        studentsViewModel = ViewModelProvider(this).get(StudentsViewModel::class.java)

        // LifeData
        initObserver()
        fetchStudentList()

        initEvent()

        // Data 요청
        studentsViewModel.fetchJobList()
    }

    private fun initObserver() {
        // Error
        studentsViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showErrorDialog(Throwable(it))
                studentsViewModel.clearErrorMessage()
            }
        }

        // Other Response
        studentsViewModel.studentList.observe(viewLifecycleOwner) { studentList ->
            studentListCache = studentList!!
        }

        studentsViewModel.jobList.observe(viewLifecycleOwner) { jobList ->
            jobListCache = jobList // ✅ LiveData 변경 시 전역 변수 업데이트
        }

        studentsViewModel.weeklyPaymentStatus.observe(viewLifecycleOwner) { response ->
            showToast("주급이 지급되었습니다")
        }

        studentsViewModel.bonusPaymentStatus.observe(viewLifecycleOwner) { response ->
            showToast("인센티브가 지급되었습니다")
        }
    }

    private fun fetchStudentList() {
        val studentHeader = listOf("번호", "이름", "직업 (월급)", "아이디", "비밀번호")

        // 행 클릭 이벤트 여부 설정 (필요하면 추가, 필요 없으면 null)
        val isRowClickable = true // 🔥 필요 없으면 false로 설정

        // 어댑터 초기화 (처음에는 빈 리스트)
        val adapter = if (isRowClickable) {
            // 클릭 이벤트를 사용하는 경우, 아래 {} 안에 작성 : Intent, Toast, ShowDialog 등....
            BaseTableAdapter(studentHeader, emptyList()) { rowIndex, rowData ->
                Log.d("TAG", "Row 클릭됨: $rowIndex, 데이터: $rowData")

                // 🔹 다이얼로그에 전달할 데이터 설정
                val bundle = Bundle().apply {
                    putString("name", rowData[1])
                    putString("job", rowData[2])
                    putString("id", rowData[3])
                    putString("password", rowData[4])
                }

                val dialog = StudentInfoDialogFragment().apply {
                    arguments = bundle // ✅ 데이터 전달
                }
                dialog.show(parentFragmentManager, "StudentInfoDialog") // ✅ 프래그먼트에서 다이얼
            }
        } else {
            // 행 클릭 비활성화
            BaseTableAdapter(studentHeader, emptyList())
        }

        binding.recyclerStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerStudents.adapter = adapter

        binding.recyclerStudents.visibility = View.VISIBLE
        binding.textNullStudent.visibility = View.GONE
        studentsViewModel.studentList.observe(viewLifecycleOwner) { studentList ->
            if(studentList.isNullOrEmpty()){
                binding.recyclerStudents.visibility = View.GONE
                binding.textNullStudent.visibility = View.VISIBLE
                return@observe
            }

            val studentList = studentList.mapIndexed { index, student ->
                val jobName = student.job?.jobName ?: "시민"
                val salary = student.job?.baseSalary ?: 0

                BaseTableRowModel(
                    listOf(
                        (index + 1).toString(),  // 번호
                        student.name ?: "N/A",        // 이름 (원래는 student.name 이었겠지만 username 사용)
                        "$jobName ($salary)",                      // 현재 직업 + 월급 정보 제공하지 않음
                        student.username,        // 아이디
                        student.teacher.password  // 비밀번호 대신 학교명 (데이터에 비밀번호 없음)
                    )
                )
            }

            // 데이터 변동 사항이 생길 경우 업데이트
            adapter.updateData(studentHeader, studentList)
        }

        // ViewModel에서 데이터 가져오기
        studentsViewModel.fetchStudentList()
    }

    private fun showStudentInfoDialog(rowData: List<String>) {
        val dialog = StudentInfoDialogFragment().apply {
            arguments = Bundle().apply {
                putString("name", rowData[1])
                putString("job", rowData[2])
                putString("id", rowData[3])
                putString("password", rowData[4])
            }
        }
        dialog.show(parentFragmentManager, "StudentInfoDialog") // ✅ 프래그먼트에서 다이얼로그 띄우기
    }


    private fun initEvent() {

        // 학생 정보 탭
        binding.btnTabStudentInfo.setOnClickListener {
            selectTab(true)
            binding.recyclerStudents.visibility = View.VISIBLE
            binding.textNullStudent.visibility = View.GONE
        }

        // 재정 상태 탭
        binding.btnTabFincialStatus.setOnClickListener {
            // if 조건 추가. 데이터가 없으면 recyclerview 가리고, 텍스트만 보이도록
            binding.recyclerStudents.visibility = View.GONE
            binding.textNullStudent.visibility = View.GONE
            selectTab(false)
            sampleDataFinance()
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
        val studentMap = studentListCache.associateBy({ it.name ?: "이름 없음" }, { it.id ?: -1 }) // 학생 데이터
        val dialog = IncentiveDialogFragment.newInstance(studentMap)

        dialog.setOnConfirmListener { studentId, price ->
            studentsViewModel.processStudentBonus(studentId, price) // ✅ API 호출
        }

        dialog.show(parentFragmentManager, "IncentiveDialog")
    }


    private fun showSingleStudentAddDialog() {
        val dialogBinding = DialogStudentRegistrationBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // job 설정
        val jobList = jobListCache.map { it.jobName }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, jobList)
        dialogBinding.editJob.adapter = adapter

        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnStudentRegistration.setOnClickListener {
            // 유효성 검사
            val name = dialogBinding.editAddName.text
            val id = dialogBinding.editId.text
            val password = dialogBinding.editPassword.text
            if(name.isNullOrEmpty() || id.isNullOrEmpty() || password.isNullOrEmpty()){
                showToast("모든 항목을 빠짐없이 입력해주세요")
                return@setOnClickListener
            }

            // 단순 데이터 추가
            val user = StudentSingleCreateRequest(username = id.toString(), name = name.toString(), password = password.toString(), profileImage = "")
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


    private fun sampleDataFinance() {
        // 컬럼 개수가 5개 일 때 사용 방법
        val header2 = listOf("번호", "이름", "계좌 잔액", "은행 가입상품", "보유 주식")
        val data2 = listOf(
            BaseTableRowModel(listOf("박지성", "user04", "34,000", "3개", "1개")),
            BaseTableRowModel(listOf("손흥민", "user05", "23,999", "1개", "2개"))
        )

        // 컬럼 개수가 5개인 테이블
        val adapter2 = BaseTableAdapter(header2, data2)
        binding.recyclerStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerStudents.adapter = adapter2
    }

    // Tab 변경 이벤트
    private fun selectTab(isStudentInfo: Boolean) {
        val context = context ?: return

        if (isStudentInfo) {
            // "학생 정보 탭" 활성화
            // 조건 추가 : 탭이 바뀔 경우 : ReyclerView 의 내용을 ViewModel 에 있는 listData로 변경해야 한다.

            binding.tvStudentInfo.setTextAppearance(R.style.heading4)
            binding.tvStudentInfo.setTextColor(ContextCompat.getColor(context, R.color.mainOrange))
            binding.underlineStudentInfo.visibility = View.VISIBLE

            // "재정 상태" 비활성화
            binding.tvFinancialStatus.setTextAppearance(R.style.body2)
            binding.tvFinancialStatus.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.lightGray
                )
            )
            binding.underlineFinancialStatus.visibility = View.INVISIBLE
        } else {
            // 학생 정보 비활성화
            binding.tvStudentInfo.setTextAppearance(R.style.body2)
            binding.tvStudentInfo.setTextColor(ContextCompat.getColor(context, R.color.lightGray))
            binding.underlineStudentInfo.visibility = View.INVISIBLE

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
