
package com.ladysparks.ttaenggrang.ui.component

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

class PieChartComponent(private val context: Context, private val pieChart: PieChart) {

    // 동적 데이터 지원: 데이터를 받아서 차트 설정
    fun setupPieChart(dataList: List<Pair<Float, String>>, colorList: List<Int>) {
        if (dataList.isEmpty() || colorList.size < dataList.size) return // 데이터 또는 색상 부족 방지

        val entries = dataList.map { PieEntry(it.first, it.second) }

        val dataSet = PieDataSet(entries, "").apply {
            colors = colorList.map { ContextCompat.getColor(context, it) }
            valueTextSize = 14f
            valueTextColor = Color.WHITE
            setValueFormatter(PercentFormatter(pieChart))
            setDrawValues(true)
        }

        val pieData = PieData(dataSet).apply {
            setValueTextSize(14f)
            setValueTextColor(Color.WHITE)
        }

        pieChart.apply {
            data = pieData
            description.isEnabled = false
            isClickable = false
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            animateY(1000)

            // 범례 설정
            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                textSize = 14f
            }
        }
    }
}
