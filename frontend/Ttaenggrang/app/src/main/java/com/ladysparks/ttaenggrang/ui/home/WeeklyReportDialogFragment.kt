package com.ladysparks.ttaenggrang.ui.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ladysparks.ttaenggrang.databinding.DialogStudentWeeklyReportBinding

class WeeklyReportDialog(context: Context) : Dialog(context) {

    private lateinit var binding: DialogStudentWeeklyReportBinding

    init {
        binding = DialogStudentWeeklyReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 다이얼로그 크기 조정
//        window?.setLayout(
//            (context.resources.displayMetrics.widthPixels * 0.7).toInt(),
//            (context.resources.displayMetrics.heightPixels * 0.8).toInt()
//        )

        // 차트 로딩
        binding.barChart.post {
            val chartManager = WeeklyReportChartManager(binding.barChart)
            chartManager.setupChart()
        }
    }
}
