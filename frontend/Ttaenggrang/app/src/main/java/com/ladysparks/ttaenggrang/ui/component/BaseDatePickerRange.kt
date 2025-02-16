package com.ladysparks.ttaenggrang.ui.component

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.ladysparks.ttaenggrang.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BaseDatePickerRange(
    context: Context,
    private val onDateSelected: (startDate: String, endDate: String) -> Unit // 시작 & 종료 날짜 전달
) : Dialog(context) {
    init {
        setContentView(R.layout.date_picker_range) // date_picker_range.xml 레이아웃 사용

        val btnConfirm = findViewById<MaterialButton>(R.id.btnConfirmDate)
        val btnClose = findViewById<MaterialButton>(R.id.btnCloseConfirmDate)
        val datePickerStart = findViewById<DatePicker>(R.id.datePickerStart)
        val datePickerEnd = findViewById<DatePicker>(R.id.datePickerEnd)


        btnClose.setOnClickListener { dismiss() }

        btnConfirm.setOnClickListener {
            val startCalendar = Calendar.getInstance().apply {
                set(datePickerStart.year, datePickerStart.month, datePickerStart.dayOfMonth)
            }
            val endCalendar = Calendar.getInstance().apply {
                set(datePickerEnd.year, datePickerEnd.month, datePickerEnd.dayOfMonth)
            }

            if (startCalendar.after(endCalendar)) {
                Toast.makeText(context, "종료 날짜는 시작 날짜보다 빠를 수 없습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDate = dateFormat.format(startCalendar.time)
            val endDate = dateFormat.format(endCalendar.time)

            onDateSelected(startDate, endDate)
            dismiss()
        }
    }

}