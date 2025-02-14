package com.ladysparks.ttaenggrang.ui.students

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.ladysparks.ttaenggrang.databinding.DialogStudentRegisterSecondBinding
import com.ladysparks.ttaenggrang.databinding.DialogStudentRegistrationBinding

class StudentInfoDialogFragment : DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.5).toInt(),  // 🔹 가로 90%로 조절
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = DialogStudentRegistrationBinding.inflate(inflater, container, false)

        // 감춤
        binding.btnClose.visibility = View.GONE
        binding.btDialogCancel.visibility = View.GONE

        binding.dialogTitle.text = "학생 정보 상세"
        binding.btnStudentRegistration.text = "확인"
        binding.btnStudentRegistration.setOnClickListener {
            dismiss()
        }

        binding.editJob.apply {
            setPadding(0, 20, 0, 20)
        }

        // 🔹 전달받은 데이터 가져오기
        val name = arguments?.getString("name") ?: ""
        val job = arguments?.getString("job") ?: ""
        val id = arguments?.getString("id") ?: ""
        val password = arguments?.getString("password") ?: ""

        // 학생 정보 설정
        binding.editAddName.setText(name)
        binding.editId.setText(id)
        binding.editPassword.setText(password)

        // 🔹 받아온 job 값 하나만 `Spinner`에 설정 (고정 리스트 제거)
        val jobList = listOf(job) // ✅ 받은 job 값을 리스트로 만듦
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, jobList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.editJob.adapter = adapter

        // 🔹 스피너 비활성화 (수정 불가능)
        binding.editJob.isEnabled = false
        binding.editJob.isClickable = false
        binding.editAddName.isEnabled = false
        binding.editId.isEnabled = false
        binding.editPassword.isEnabled = false

        // 닫기 버튼 클릭
        binding.btnClose.setOnClickListener { dismiss() }

        return binding.root
    }
}
