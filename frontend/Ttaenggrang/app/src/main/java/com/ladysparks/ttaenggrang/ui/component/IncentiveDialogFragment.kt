package com.ladysparks.ttaenggrang.ui.component

import android.R
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import com.ladysparks.ttaenggrang.databinding.DialogIncentiveBinding

class IncentiveDialogFragment: DialogFragment() {

    private var listener: ((Int, Int) -> Unit)? = null // ✅ 선택된 학생 ID와 금액을 콜백으로 전달

    fun setOnConfirmListener(listener: (studentId: Int, price: Int) -> Unit) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBinding = DialogIncentiveBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // 1. 학생 데이터 가져오기
        val studentMap = arguments?.getSerializable("STUDENT_MAP") as? Map<String, Int> ?: emptyMap()
        val studentNames = studentMap.keys.toList()

        // 1. 학생 데이터 가져오기 :  일반 프래그먼트에서 진행할 겨우
//        val studentMap = studentListCache.associateBy({ it.name ?: "이름 없음" }, { it.id })
//        val studentNames = studentMap.keys.toList()


        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, studentNames)
        dialogBinding.studentSpinner.adapter = adapter
        dialogBinding.btDialogCancel.setOnClickListener {
            dismiss() // 취소 버튼 클릭 시 다이얼로그 닫기
        }
        dialogBinding.btnDialogConfirm.setOnClickListener {
            val selectedStudent = dialogBinding.studentSpinner.selectedItem.toString()
            val price = dialogBinding.editBonus.text.toString().toIntOrNull() ?: 0

            // 1. 선택된 학생 ID 와 금액 전달
            listener?.invoke(studentMap[selectedStudent]!!, price) // 선택된 학생 ID와 금액 전달

            // 2. 선택된 학생 ID와 금액 전달 : 일반 프래그먼트에서 할 경우
            //studentsViewModel.processStudentBonus(studentId = studentMap[selectedStudent]!!, incentive = price ?: 0)

            dismiss()
        }

        return dialog
    }

    companion object {
        fun newInstance(studentMap: Map<String, Int>): IncentiveDialogFragment {
            val fragment = IncentiveDialogFragment()
            val args = Bundle()
            args.putSerializable("STUDENT_MAP", HashMap(studentMap)) // ✅ 학생 데이터 전달
            fragment.arguments = args
            return fragment
        }
    }
}