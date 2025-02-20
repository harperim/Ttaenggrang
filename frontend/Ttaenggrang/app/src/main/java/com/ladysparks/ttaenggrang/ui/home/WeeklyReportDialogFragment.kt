package com.ladysparks.ttaenggrang.ui.home

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import coil.load
import coil.size.ViewSizeResolver
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.data.model.response.WeekReportStudentGrowth
import com.ladysparks.ttaenggrang.data.model.response.WeekReportSummaryResponse
import com.ladysparks.ttaenggrang.databinding.DialogStudentWeeklyReportBinding
import com.ladysparks.ttaenggrang.util.NumberUtil
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import java.io.File

class WeeklyReportDialog(
    context: Context,
    weekReportSummaryData: WeekReportSummaryResponse,
    weekReportAiFeedback: String,
    weekGrowth: WeekReportStudentGrowth
) : Dialog(context) {

    private lateinit var binding: DialogStudentWeeklyReportBinding

    init {
        binding = DialogStudentWeeklyReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 날짜 설정
        binding.textDialogContent.text = weekReportSummaryData.reportDate

        // 이번주 금융 성적표(막대 차트)
        binding.chartBar.post {
            val chartManager = WeeklyReportChartManager(context, binding.chartBar)
            chartManager.setupChart(weekGrowth)
        }

        // 파이 차트 설정
        binding.chartPie.post {
            setupPieChart(weekReportSummaryData)
        }

        // 금융 활동 요약
        binding.textTotalIncome.text = NumberUtil.formatWithComma(weekReportSummaryData.totalIncome)
        binding.textSalaryAmount.text = NumberUtil.formatWithComma(weekReportSummaryData.salaryAmount)
        binding.textSavingsAmount.text = NumberUtil.formatWithComma(weekReportSummaryData.savingsAmount)
        binding.textInvestmentProfit.text = NumberUtil.formatWithComma(weekReportSummaryData.investmentReturn)
        binding.textIncentiveAmount.text = NumberUtil.formatWithComma(weekReportSummaryData.incentiveAmount)
        binding.textExpenseAmount.text = NumberUtil.formatWithComma(weekReportSummaryData.totalExpenses)

        // Progress Bar
        val progressValue = SharedPreferencesUtil.getValue(SharedPreferencesUtil.MY_ACHIEVEMENT_RATE, 0)
        binding.progressbar.progress = progressValue.coerceIn(0, 100)
        binding.textProgressbar.text = "${progressValue} %"
        binding.textRank.text = "${SharedPreferencesUtil.getValue(SharedPreferencesUtil.MY_RANK, 1)} 위"

        // AI 피드백
        binding.textAiFeedback.text = weekReportAiFeedback
        binding.imgFox.load(File("/storage/emulated/0/Download/logo4.jpg"))

        // button Event
        binding.btnStudentRegistration.setOnClickListener {
            dismiss()
        }

        binding.imgFox.load(R.drawable.logo3){
            crossfade(true)
            size(ViewSizeResolver(binding.imgFox))
        }
    }

    // PieChart 설정 함수
    private fun setupPieChart(weekReportSummaryData: WeekReportSummaryResponse) {
        val categoryData = listOf(
            "급여" to weekReportSummaryData.totalIncome,
            "저축" to weekReportSummaryData.savingsAmount,
            "투자" to weekReportSummaryData.investmentReturn,
            "인센티브" to weekReportSummaryData.incentiveAmount,
            "소비" to weekReportSummaryData.totalExpenses
        )

        val totalAmount = categoryData.sumOf { it.second }.toFloat().takeIf { it > 0 } ?: 1f
        val dataList = categoryData.map { (name, value) -> (value / totalAmount) * 100 to name }

        if (dataList.isEmpty()) return

        val colorList = generateColorList(5)
        val entries = dataList.map { PieEntry(it.first, it.second) }

        val dataSet = PieDataSet(entries, "").apply {
            colors = colorList.map { ContextCompat.getColor(context, it) }
            valueTextSize = 14f
            valueTextColor = Color.WHITE
            setValueFormatter(PercentFormatter(binding.chartPie))
            setDrawValues(true)
        }

        val pieData = PieData(dataSet).apply {
            setValueTextSize(12f)
            setValueTextColor(Color.WHITE)
        }

        binding.chartPie.apply {
            data = pieData
            description.isEnabled = false
            isClickable = false
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            animateY(1000)

            setExtraOffsets(10f, 10f, 60f, 10f)
            setHoleRadius(60f)
            setTransparentCircleRadius(60f)

            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.VERTICAL
                textSize = 14f
                yOffset = -50f
                setDrawInside(true)
                xOffset = 70f
            }

            requestLayout()
            invalidate()
        }
    }

    private fun generateColorList(size: Int): List<Int> {
        val predefinedColors = listOf(
            R.color.chartBlue,
            R.color.chartPink,
            R.color.chartPurple,
            R.color.chartGreen,
            R.color.chartYellow
        )
        return List(size) { predefinedColors[it % predefinedColors.size] }
    }
}
