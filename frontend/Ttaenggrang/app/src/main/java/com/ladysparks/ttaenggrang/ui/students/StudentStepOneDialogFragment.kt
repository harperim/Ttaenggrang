package com.ladysparks.ttaenggrang.ui.students

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ladysparks.ttaenggrang.databinding.DialogStudentRegisterFirstBinding

class StudentStepOneDialogFragment(private val viewModel: StudentsViewModel) : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DialogStudentRegisterFirstBinding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            val count = binding.editPeople.text.toString().toIntOrNull() ?: 0
            val prefix = binding.etStudentPrefix.text.toString()

            viewModel.studentCount.value = count
            viewModel.studentPrefix.value = prefix

            // 2단계 다이얼로그 표시
            val stepTwoDialog = StudentStepTwoDialogFragment(viewModel)
            stepTwoDialog.show(parentFragmentManager, "StepTwoDialog")
            dismiss()
        }

        return binding.root
    }
}