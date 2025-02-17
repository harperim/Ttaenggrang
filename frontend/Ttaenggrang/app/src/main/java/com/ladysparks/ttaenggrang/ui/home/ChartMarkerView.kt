package com.ladysparks.ttaenggrang.ui.home

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.util.NumberUtil

class ChartMarkerView(context: Context) : MarkerView(context, R.layout.marker_view) {
    private val textView: TextView = findViewById(R.id.marker_text)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        textView.text = "값: ${NumberUtil.formatWithComma(e?.y?.toInt() ?: 0)}"  // ✅ 값 표시 (소수점 제거)
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-width / 2).toFloat(), (-height - 10).toFloat()) // ✅ 위치 조정
    }
}
