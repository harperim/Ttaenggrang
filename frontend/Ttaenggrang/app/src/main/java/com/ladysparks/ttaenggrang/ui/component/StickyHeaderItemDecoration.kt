package com.ladysparks.ttaenggrang.base

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.R

class StickyHeaderDecoration(private val adapter: BaseTableHeaderStickyAdapter) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        // ✅ 첫 번째 아이템에만 헤더 높이 만큼 여백 추가
        if (position == 0) {
            outRect.top = Math.min(view.resources.getDimensionPixelSize(R.dimen.sticky_header_height), view.height)

        } else {
            outRect.top = 0 // ✅ 나머지 아이템에는 추가 여백 없음
        }

        // ✅ 마지막 아이템의 하단 여백 제거
        if (position == parent.adapter?.itemCount?.minus(1)) {
            outRect.bottom = 0 // ✅ 마지막 아이템이 불필요한 여백을 가지지 않도록 설정
        }
    }

    override fun onDrawOver(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val child = parent.getChildAt(0) ?: return
        val position = parent.getChildAdapterPosition(child)
        if (position == RecyclerView.NO_POSITION) return

        val headerViewHolder = adapter.onCreateStickyHeaderViewHolder(parent)
        adapter.onBindStickyHeaderViewHolder(headerViewHolder, position)
        val headerView = headerViewHolder.itemView

        // ✅ 헤더 뷰 크기 측정 및 배치
        headerView.measure(
            View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(child.height, View.MeasureSpec.AT_MOST)
        )
        headerView.layout(0, 0, parent.width, headerView.measuredHeight)

        // ✅ 헤더를 캔버스에 그림 (고정 위치)
        c.save()
        c.translate(0f, 0f)
        headerView.draw(c)
        c.restore()
    }
}
