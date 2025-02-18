package com.ladysparks.ttaenggrang.ui.home

import android.graphics.Color
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class WeeklyReportChartManager(private val barChart: BarChart) {

    fun setupChart() {
        val labels = listOf("저축 증가율", "투자 수익률", "지출 증가율")

        // ✅ 하드코딩된 데이터 (테스트용)
        val entriesLastWeek = listOf(
            BarEntry(0f, 29f),
            BarEntry(1f, 28f),
            BarEntry(2f, 96f)
        )

        val entriesThisWeek = listOf(
            BarEntry(0.3f, 45f),
            BarEntry(1.3f, 89f),
            BarEntry(2.3f, 85f)
        )

        val entriesMidThisWeek = listOf(
            BarEntry(0.6f, 59f),
            BarEntry(1.6f, 70f),
            BarEntry(2.6f, 84f)
        )

        val dataSetLastWeek = BarDataSet(entriesLastWeek, "지난주 내 통계").apply {
            color = Color.BLUE
        }
        val dataSetThisWeek = BarDataSet(entriesThisWeek, "이번주 내 통계").apply {
            color = Color.GREEN
        }
        val dataSetMidThisWeek = BarDataSet(entriesMidThisWeek, "이번주 반 통계").apply {
            color = Color.rgb(255, 165, 0) // 주황색
        }

        val barData = BarData(dataSetLastWeek, dataSetThisWeek, dataSetMidThisWeek)
        barData.barWidth = 0.25f // 막대 너비 조절

        // X축 설정
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)

        // X축 최소, 최대 설정
        xAxis.axisMinimum = -0.5f // 0번째 데이터가 잘리지 않도록 설정
        xAxis.axisMaximum = labels.size.toFloat() // 마지막 인덱스까지 표시

        // Y축 설정
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false

        // 범례 설정
        val legend = barChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.setDrawInside(false)

        // 차트 데이터 확인용 로그
        Log.d("ChartDebug", "Entries Count: ${barData.entryCount}")

        // 차트에 데이터 적용
        barChart.data = barData
        barChart.groupBars(0f, 0.4f, 0.05f) // 그룹 막대 정렬
        barChart.setFitBars(true)
        barChart.description.isEnabled = false

        barChart.data.notifyDataChanged()
        barChart.notifyDataSetChanged()
        barChart.invalidate() // 다시 그리기
    }
}
