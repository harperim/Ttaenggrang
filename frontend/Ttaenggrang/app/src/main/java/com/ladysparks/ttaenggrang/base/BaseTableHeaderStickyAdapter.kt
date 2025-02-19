package com.ladysparks.ttaenggrang.base

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel

class BaseTableHeaderStickyAdapter(
    private var header: List<String>, // ✨ 헤더 컬럼 리스트 (동적 설정)
    private var data: List<BaseTableRowModel>, // ✨ 유동적인 데이터 리스트
    private var columnWeights: List<Float> = List(header.size) { 1f }, // 가중치 기본값 1f
    private val onRowClickListener: ((rowIndex: Int, rowData: List<String>) -> Unit)? = null // ✅ 행 클릭 리스너 추가
) : RecyclerView.Adapter<BaseTableHeaderStickyAdapter.ItemViewHolder>(),
    StickyHeaderAdapter<BaseTableHeaderStickyAdapter.HeaderViewHolder> {

    companion object {
        private const val VIEW_TYPE_ITEM = 1 // ✅ 일반 행 타입 추가
    }


    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_ITEM // ✅ 모든 아이템을 일반 행으로 처리
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_table_row_dynamic, parent, false)
        return ItemViewHolder(view, columnWeights, onRowClickListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(data[position].data, position) // ✅ position 수정 (헤더 제거)
        }
    }
    override fun getItemCount(): Int = data.size // ✅ 헤더를 포함하지 않도록 수정

    // ✅ StickyHeader 적용을 위한 함수들
    override fun getStickyHeaderId(position: Int): Long {
        return if (data.isNotEmpty()) 1L else RecyclerView.NO_ID // ✅ 모든 아이템이 같은 헤더를 사용하도록 설정
    }

    override fun onCreateStickyHeaderViewHolder(parent: ViewGroup): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_table_header_dynamic, parent, false)

        return HeaderViewHolder(view, columnWeights)
    }

    override fun onBindStickyHeaderViewHolder(viewHolder: HeaderViewHolder, position: Int) {
        viewHolder.bind(header) // ✅ 항상 동일한 헤더 바인딩
    }

    // ✅ HeaderViewHolder (StickyHeader 전용)
    class HeaderViewHolder(itemView: View, private val columnWeights: List<Float>) : RecyclerView.ViewHolder(itemView) {
        private val container: LinearLayout = itemView.findViewById(R.id.headerContainer)

        fun bind(headerData: List<String>) {
            container.removeAllViews()
            for ((index, text) in headerData.withIndex()) {
                val weight = columnWeights.getOrElse(index) { 1f }
                val textView = TextView(itemView.context).apply {
                    this.text = text
                    setTextAppearance(R.style.heading4)
                    setPadding(16, 8, 16, 8)
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight)
                }
                container.addView(textView)
            }
        }
    }

        // ✅ ItemViewHolder (데이터 행)
        class ItemViewHolder(itemView: View, private val columnWeights: List<Float>, private val onRowClickListener: ((rowIndex: Int, rowData: List<String>) -> Unit)?)
            : RecyclerView.ViewHolder(itemView) {
            private val container: LinearLayout = itemView.findViewById(R.id.rowContainer)

            fun bind(rowData: List<String>, rowIndex: Int) {
                container.removeAllViews() // 기존 뷰 초기화

                for ((index, text) in rowData.withIndex()) {
                    val weight = columnWeights.getOrElse(index) { 1f }
                    val textView = TextView(itemView.context).apply {
                        this.text = text
                        //setPadding(16, 8, 16, 8)
                        minimumHeight = 48
                        gravity = Gravity.CENTER
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight)
                    }
                    container.addView(textView)
                }
                itemView.setOnClickListener {
                    onRowClickListener?.invoke(rowIndex, rowData)
                }
            }
        }

    fun updateData(newRows: List<BaseTableRowModel>, recyclerView: RecyclerView) {
        data = newRows
        notifyDataSetChanged()
    }
}
