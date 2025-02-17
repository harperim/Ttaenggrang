import android.graphics.*
import android.text.style.ReplacementSpan

class RoundedBackgroundSpan(private val backgroundColor: Int, private val textColor: Int) : ReplacementSpan() {

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val textWidth = paint.measureText(text, start, end).toInt()
        return textWidth + 40 // 좌우 여백 추가
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        if (text == null) return

        val originalColor = paint.color

        // 📌 FontMetrics을 사용하여 높이 계산
        val textWidth = paint.measureText(text, start, end)
        val textHeight = paint.descent() - paint.ascent()

        val radius = textHeight / 2f + 8f // 둥근 모서리 크기
        val rectF = RectF(x, y + paint.ascent() - 8f, x + textWidth + 40, y + paint.descent() + 8f)

        // 배경 색상 적용
        paint.color = backgroundColor
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(rectF, radius, radius, paint)

        // 텍스트 색상 적용
        paint.color = textColor
        paint.style = Paint.Style.FILL
        canvas.drawText(text, start, end, x + 20, y.toFloat(), paint)

        // 원래 색상 복원
        paint.color = originalColor
    }
}
