package com.ladysparks.ttaenggrang.ui.home

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.data.model.response.WeekReportStudentGrowth

class WeeklyReportChartManager(val context: Context, private val barChart: BarChart) {

    fun setupChart(weekGrowth: WeekReportStudentGrowth) {
        val labels = listOf("저축 증가율", "투자 수익률", "지출 증가율")

        val entriesLastWeek = listOf(
            BarEntry(0f, weekGrowth.lastWeekSummary.savingsGrowthRate.toFloat()),  // 저축 증가율
            BarEntry(1f, weekGrowth.lastWeekSummary.investmentReturnRate.toFloat()), // 투자 수익률
            BarEntry(2f, weekGrowth.lastWeekSummary.expenseGrowthRate.toFloat()) // 지출 증가율
        )

        val entriesThisWeek = listOf(
            BarEntry(0.3f, weekGrowth.thisWeekSummary.savingsGrowthRate.toFloat()), // 저축 증가율
            BarEntry(1.3f, weekGrowth.thisWeekSummary.investmentReturnRate.toFloat()), // 투자 수익률
            BarEntry(2.3f, weekGrowth.thisWeekSummary.expenseGrowthRate.toFloat()) // 지출 증가율
        )

        val entriesMidThisWeek = listOf(
            BarEntry(0.6f, weekGrowth.classAverageSummary.savingsGrowthRate.toFloat()), // 저축 증가율
            BarEntry(1.6f, weekGrowth.classAverageSummary.investmentReturnRate.toFloat()), // 투자 수익률
            BarEntry(2.6f, weekGrowth.classAverageSummary.expenseGrowthRate.toFloat()) // 지출 증가율
        )


        val dataSetLastWeek = BarDataSet(entriesLastWeek, "지난주 내 통계").apply {
            color = ContextCompat.getColor(context, R.color.chartBlue)
        }
        val dataSetThisWeek = BarDataSet(entriesThisWeek, "이번주 내 통계").apply {
            color =  ContextCompat.getColor(context, R.color.chartGreen)
        }
        val dataSetMidThisWeek = BarDataSet(entriesMidThisWeek, "이번주 반 통계").apply {
            color =  ContextCompat.getColor(context, R.color.chartOrange)
        }

        val barData = BarData(dataSetLastWeek, dataSetThisWeek, dataSetMidThisWeek)
        barData.barWidth = 0.22f // 막대 너비 조절

        // X축 설정
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)  // X축 점선 활성화
        xAxis.gridColor = ContextCompat.getColor(context, R.color.foundation_black_100)  // X축 점선 색상 설정
        xAxis.enableGridDashedLine(5f, 10f, 0f)  // 점선 간격 (10px 선, 10px 공백)
        xAxis.granularity = 0.5f
        xAxis.setCenterAxisLabels(true)
        xAxis.yOffset = 10f  // 기본값보다 여백을 더 추가
        barChart.extraBottomOffset = 10f

        // X축 최소, 최대 설정
        xAxis.axisMinimum = -0.2f // 0번째 데이터가 잘리지 않도록 설정
        xAxis.axisMaximum = labels.size.toFloat() // 마지막 인덱스까지 표시

        // Y축 설정
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        barChart.axisLeft.setDrawGridLines(true)  // Y축 점선 활성화
        barChart.axisLeft.gridColor = ContextCompat.getColor(context, R.color.foundation_black_100)  // 축 점선 색상 설정
        barChart.axisLeft.enableGridDashedLine(5f, 10f, 0f)  // 점선 간격 (10px 선, 10px 공백)


        // 범례 설정
        val legend = barChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.setDrawInside(false)


        // 차트에 데이터 적용
        barChart.data = barData
        barChart.groupBars(0f, 0.2f, 0.03f) // 그룹 막대 정렬
        barChart.setFitBars(true)
        barChart.description.isEnabled = false

        barChart.data.notifyDataChanged()
        barChart.notifyDataSetChanged()
        barChart.invalidate() // 다시 그리기
    }
}
