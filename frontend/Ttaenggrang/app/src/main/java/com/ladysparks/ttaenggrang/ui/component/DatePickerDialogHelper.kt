package com.ladysparks.ttaenggrang.ui.component

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import com.ladysparks.ttaenggrang.R
import java.text.SimpleDateFormat
import java.util.*

object DatePickerDialogHelper {

    /**
     * 날짜 선택 다이얼로그를 띄우는 함수
     * @param context : 호출하는 액티비티의 Context
     * @param isStartTime : 시작 시간인지 여부 (true = 00:01:01, false = 23:59:59)
     * @param onDateSelected : 날짜 선택 후 Activity로 전달하는 콜백 함수
     */
    fun showDatePickerDialog(
        context: Context,
        isStartTime: Boolean,
        onDateSelected: (String) -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.date_picker, null)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.datePickerSelect)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmDate)
        val btnClose = dialogView.findViewById<Button>(R.id.btnCloseConfirmDate)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Dialog 크기 설정
        dialog.setOnShowListener {
            val window = dialog.window
            //  원래 코드
//            window?.setLayout(800, ViewGroup.LayoutParams.WRAP_CONTENT) // ✅ 500dp 고정

            // 원래 코드가 픽셀 단위라고 해서 dp로 변환한 코드
            val scale = context.resources.displayMetrics.density
            val widthInPx = (550 * scale + 0.5f).toInt() // 300dp → px 변환
            window?.setLayout(widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT)

        }

        btnConfirm.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)

            // 선택된 날짜를 포맷하여 반환
            val formattedDate = formatDateTime(calendar.time, isStartTime)

            // Activity로 선택된 날짜 전달
            onDateSelected(formattedDate)

            dialog.dismiss()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * 선택된 날짜를 "yyyy-MM-dd HH:mm:ss" 형식으로 변환하는 함수
     * @param date : 선택된 Date 객체
     * @param isStartTime : 시작 시간인지 여부 (true = 00:01:01, false = 23:59:59)
     * @return 변환된 날짜 문자열 (예: 2025-02-14 00:01:01)
     */
    private fun formatDateTime(date: Date, isStartTime: Boolean): String {
        val format = "yyyy-MM-dd HH:mm:ss"
        val sdf = SimpleDateFormat(format, Locale.getDefault())

        val calendar = Calendar.getInstance()
        calendar.time = date

        if (isStartTime) {
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 1)
            calendar.set(Calendar.SECOND, 1)
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
        }

        return sdf.format(calendar.time)
    }
}