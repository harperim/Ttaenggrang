package com.ladysparks.ttaenggrang.ui.students

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.databinding.DialogStudentRegistrationBinding
import com.ladysparks.ttaenggrang.util.showToast

class StudentDetailDialog : DialogFragment() {
    private val viewModel: StudentsViewModel by activityViewModels() // ✅ MainViewModel 가져오기
    private var _binding: DialogStudentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.5).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogStudentRegistrationBinding.inflate(inflater, container, false)


        val name = arguments?.getString("name") ?: ""
        val job = arguments?.getString("job") ?: ""
        val id = arguments?.getString("id") ?: ""
        val password = arguments?.getString("password") ?: ""

        val jobList = arguments?.getStringArrayList("jobList") ?: arrayListOf() // MainFragment에서 전달한 jobList
        val jobIdList = arguments?.getStringArrayList("jobId")?.map { it.toInt() } ?: emptyList()

        binding.dialogTitle.text = "학생 상세정보"

        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnClose.setOnClickListener { dismiss() }

        binding.editAddName.apply {
            setText(name)
            isEnabled = false
        }
        binding.editId.apply {
            setText(id)
            isEnabled = false
        }
        binding.editPassword.apply {
            setText(password)
            isEnabled = false
        }

        binding.editJob.apply {
            setPadding(0, 20, 0, 20)
        }

        // MainViewModel의 jobList를 Spinner에 설정
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, jobList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.editJob.adapter = adapter

        // 기존 직업 선택
        val jobIndex = jobList.indexOf(job)
        if (jobIndex != -1) {
            binding.editJob.setSelection(jobIndex)
        }

        // API 호출 버튼 이벤트 추가
        binding.btnStudentRegistration.setOnClickListener {
            // 선택된 직업 id 얻기
            val jobPosition = binding.editJob.selectedItemPosition
            val selectedJobId = jobIdList[jobPosition]

            val studentId = arguments?.getString("studentId")?.toIntOrNull()

            showToast("${studentId} , ${selectedJobId}")
            viewModel.editStudentJob(studentId!!, selectedJobId)
            dismiss()
        }


        return binding.root
    }

    companion object {
        fun newInstance(rowData: List<String>, jobList: List<JobDto>) = StudentDetailDialog().apply {
            arguments = Bundle().apply {
                putString("studentId", rowData[0])
                putString("name", rowData[1])
                putString("job", rowData[2])
                putString("id", rowData[3])
                putString("password", rowData[4])

                // jobList를 String 리스트로 변환하여 전달
                val jobNameList = ArrayList(jobList.map { it.jobName })
                val jobIdList = ArrayList(jobList.map { it.id.toString() })
                putStringArrayList("jobList", jobNameList)
                putStringArrayList("jobId", jobIdList)
            }
        }
    }
}
