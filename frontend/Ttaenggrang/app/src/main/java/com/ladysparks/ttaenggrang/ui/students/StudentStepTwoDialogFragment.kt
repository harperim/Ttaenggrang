package com.ladysparks.ttaenggrang.ui.students

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.ladysparks.ttaenggrang.databinding.DialogStudentRegisterSecondBinding

class StudentStepTwoDialogFragment(private val viewModel: StudentsViewModel) : DialogFragment() {

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.uploadFile(it, requireContext())  // 🔹 ViewModel에 업로드 요청
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DialogStudentRegisterSecondBinding.inflate(inflater, container, false)

        binding.btnFileUpload.setOnClickListener {
//            filePickerLauncher.launch(arrayOf("text/csv", "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            filePickerLauncher.launch("*/*")  // CSV 또는 XLSX 선택 가능
        }

        //  파일 업로드 후 버튼 텍스트 변경
        viewModel.uploadedFileName.observe(viewLifecycleOwner) { fileName ->
            binding.btnFileUpload.text = fileName
        }

        binding.btnSaveData.setOnClickListener {
            viewModel.sendStudentDataToServer(requireContext())
            Toast.makeText(context, "저장 요청 완료!", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return binding.root
    }
}
