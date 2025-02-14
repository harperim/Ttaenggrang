package com.ladysparks.ttaenggrang.ui.component

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.ladysparks.ttaenggrang.R

class LineChartComponent @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LineChart(context, attrs) {

    init {
        setupChart()
    }

    // 기본 스타일 설정
    private fun setupChart() {
        this.setBackgroundColor(Color.WHITE)
        this.description.isEnabled = true // 설명 활성화
        this.setTouchEnabled(true) // 터치 가능
        this.isDragEnabled = true // 드래그 가능
        this.setScaleEnabled(false) // 줌 가능
        this.setPinchZoom(true) // 핀치 줌 활성화

        // X축 설정
        val xAxis = this.xAxis
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // Y축 설정
        val yAxis = this.axisLeft
        this.axisRight.isEnabled = false // 오른쪽 Y축 비활성화
        yAxis.enableGridDashedLine(10f, 10f, 0f)
        yAxis.axisMaximum = 10000f //y축 최대값
        yAxis.axisMinimum = 0f //y축 최소값
    }

    //차트 데이터
    fun setChartData(stockHistory: List<Pair<Float, Float>>, dateLabels: List<String>, chartColor: Int) {
        val values = stockHistory.map { (x, y) -> Entry(x, y) }  // ✅ 서버 데이터 변환

        // ✅ 주식 가격 최대값 & 최소값 계산
        val maxPrice = values.maxOf { it.y }
        val minPrice = values.minOf { it.y }

        // ✅ 차트 Y축 설정 (최댓값, 최솟값 기준)
        this.axisLeft.apply {
            axisMaximum = maxPrice + (maxPrice * 0.1f) // 최대값 + 10% 여유
            axisMinimum = minPrice - (minPrice * 0.1f) // 최소값 - 10% 여유
            textColor = Color.BLACK // Y축 글자 색상
            textSize = 12f // Y축 글자 크기
        }

        val set1: LineDataSet

        if (this.data != null && this.data.dataSetCount > 0) {
            set1 = this.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            this.data.notifyDataChanged()
            this.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values, "")

            // ✅ 차트 스타일 설정
            set1.color = ContextCompat.getColor(context, chartColor)
            set1.setCircleColor(ContextCompat.getColor(context, chartColor))
            set1.lineWidth = 2f
            set1.circleRadius = 5f
            set1.setDrawCircleHole(false) // 값에 흰색표시
            set1.setDrawValues(true) // 값 표시

//            // ✅ 그라데이션 효과 추가
//            if (Utils.getSDKInt() >= 18) {
//                val drawable = ContextCompat.getDrawable(context, R.drawable.btn_rounded_full)
//                set1.fillDrawable = drawable
//            } else {
//                set1.fillColor = ContextCompat.getColor(context, chartColor)
//            }

            // ✅ 부드러운 곡선 적용
            set1.mode = LineDataSet.Mode.CUBIC_BEZIER
            set1.cubicIntensity = 0.14f // 부드러운 정도 (낮을수록 직선에 가까움)

            val dataSets = ArrayList<ILineDataSet>().apply {
                add(set1)
            }
            this.data = LineData(dataSets)

            // ✅ X축에 속성
            this.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(dateLabels) // ✅ X축에 날짜 표시
                granularity = 1f // 한 개 단위로 이동하도록 설정
                position = XAxis.XAxisPosition.BOTTOM // X축을 아래로 설정
                setDrawGridLines(false) // X축의 가이드라인 제거
                //labelRotationAngle = -45f // X축 날짜가 겹치지 않도록 기울임
                textColor = Color.BLACK // X축 글자 색상
                textSize = 12f // X축 글자 크기
                axisMinimum = -0.3f // ✅ 첫 번째 값이 살짝 오른쪽으로 이동
                //setAvoidFirstLastClipping(true) // ✅ 첫 번째 값이 Y축과 겹치지 않도록 조정
            }

        }
        // ✅ 차트 여백 추가
        this.setExtraOffsets(0f, 0f, 0f, 10f) // ✅ 왼쪽 여백 추가

        // ✅ X축과 Y축의 라벨 추가
        this.description.apply {
            text = "X축: 날짜 | Y축: 가격" // ✅ 설명 추가
            textSize = 12f
            textColor = Color.DKGRAY
        }

        // ✅ 범례(legend) 설정
        this.legend.apply {
            isEnabled = false // 범례 활성화
            textSize = 12f
            textColor = Color.BLACK
            form = Legend.LegendForm.LINE // 선 모양 범례
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER // 가운데 정렬
        }

        this.invalidate()
    }


    // ✅ 미니 차트 전용 설정 함수
    private fun setupMiniChart() {
        this.setTouchEnabled(false) // 터치 비활성화
        this.isDragEnabled = false
        this.setScaleEnabled(false)
        this.setPinchZoom(false)
        this.setDrawGridBackground(false)

        // ✅ X축, Y축, 범례, 설명 모두 제거
        this.xAxis.isEnabled = false
        this.axisLeft.isEnabled = false
        this.axisRight.isEnabled = false
        this.description.isEnabled = false
        this.legend.isEnabled = false

        // ✅ 마진 최소화 (차트가 꽉 차 보이도록)
        this.setViewPortOffsets(0f, 0f, 0f, 0f)
    }

    // ✅ 미니 차트 데이터 설정
    fun setUpMiniChart(stockHistory: List<Pair<Float, Float>>) {
        val values = stockHistory.map { (x, y) -> Entry(x, y) }

        // ✅ 주식 가격 최대값 & 최소값 계산
        val maxPrice = values.maxOf { it.y }
        val minPrice = values.minOf { it.y }

        // ✅ 차트 Y축 설정 (최댓값, 최솟값 기준)
        this.axisLeft.apply {
            axisMaximum = maxPrice + (maxPrice * 0.1f) // 최대값 + 10% 여유
            axisMinimum = minPrice - (minPrice * 0.1f) // 최소값 - 10% 여유
        }

        // ✅ 미니 차트 전용 설정 적용
        setupMiniChart()

        // ✅ 오늘 가격과 어제 가격 비교
        val yesterdayPrice = values[values.size - 2].y // 어제 가격
        val todayPrice = values.last().y // 오늘 가격

        val color = if (todayPrice > yesterdayPrice) Color.parseColor("#4CAF50") else Color.parseColor("#E53935") // 상승하면 초록색, 하락하면 빨간색


        val set1 = LineDataSet(values, "").apply {
            this.color = color // ✅ 상승/하락 색상 적용
            setCircleColor(color)
            lineWidth = 1.5f
            circleRadius = 0f
            setDrawCircles(false)
            setDrawValues(false)
            setDrawFilled(false)
            setDrawCircleHole(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.05f
        }

        val dataSets = ArrayList<ILineDataSet>().apply { add(set1) }
        this.data = LineData(dataSets)

        this.invalidate() // ✅ 차트 다시 그리기
    }

}
