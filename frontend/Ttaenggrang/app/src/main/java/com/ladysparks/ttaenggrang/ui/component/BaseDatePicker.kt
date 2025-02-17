package com.ladysparks.ttaenggrang.ui.component


import android.app.Dialog
import android.content.Context
import android.widget.DatePicker
import com.google.android.material.button.MaterialButton
import com.ladysparks.ttaenggrang.R



class BaseDatePickerDialog(
    context: Context,
    private val onDateSelected: (year: Int, month: Int, day: Int) -> Unit, //날짜 확인 버튼
    private val onCloseClick: (() -> Unit)? = null // 닫기 버튼
) : Dialog(context) {

    init {
        setContentView(R.layout.date_picker)
        val btnConfirm = findViewById<MaterialButton>(R.id.btnConfirmDate)
        val btnClose = findViewById<MaterialButton>(R.id.btnCloseConfirmDate)
        val datePicker = findViewById<DatePicker>(R.id.datePickerSelect)


        btnClose.setOnClickListener {
            onCloseClick?.invoke()
            dismiss()
        }

        btnConfirm.setOnClickListener {
            val year = datePicker.year
            val month = datePicker.month
            val day = datePicker.dayOfMonth

            onDateSelected(year, month, day)
            dismiss()
        }
    }
}