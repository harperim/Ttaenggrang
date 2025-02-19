package com.ladysparks.ttaenggrang.base

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel

class BaseTableAdapter(
    private var header: List<String>, // ✨ 헤더 컬럼 리스트 (동적 설정)
    private var data: List<BaseTableRowModel>, // ✨ 유동적인 데이터 리스트
    private var columnWeights: List<Float> = List(header.size) { 1f }, //가중치 기본값 1f
    private val onRowClickListener: ((rowIndex: Int, rowData: List<String>) -> Unit)? = null // ✅ 행 클릭 리스너 추가
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    // Header 영역과 데이터 row 영역을 구분하는 코드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_table_header_dynamic, parent, false)
            HeaderViewHolder(view, columnWeights)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_table_row_dynamic, parent, false)
            ItemViewHolder(view, columnWeights, onRowClickListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(header)
        } else if (holder is ItemViewHolder) {
            holder.bind(data[position - 1].data, position - 1)
        }
    }

    override fun getItemCount(): Int = data.size + 1 // 헤더 포함

    // 동적으로 Header 개수에 따라 TextView 를 추가로 생성
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

    class ItemViewHolder(itemView: View, private val columnWeights: List<Float>, private val onRowClickListener: ((rowIndex: Int, rowData: List<String>) -> Unit)?) : RecyclerView.ViewHolder(itemView) {
        private val container: LinearLayout = itemView.findViewById(R.id.rowContainer)

        fun bind(rowData: List<String>, rowIndex: Int) {
            container.removeAllViews() // 기존 뷰 초기화
            for ((index, text) in rowData.withIndex()) {
                val weight = columnWeights.getOrElse(index) { 1f }
                val textView = TextView(itemView.context).apply {
                    this.text = text
                    setPadding(16, 8, 16, 8)
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight)

                }
                container.addView(textView)
            }

            // ✅ 클릭 이벤트가 필요한 경우만 설정
            itemView.setOnClickListener {
                onRowClickListener?.invoke(rowIndex, rowData)
            }
        }
    }

    fun updateData(newRows: List<BaseTableRowModel>) {
        data = newRows
        notifyDataSetChanged() // ✅ RecyclerView 새로고침
    }

    // 데이터 업데이트용
    fun updateData(newHeaders: List<String>, newRows: List<BaseTableRowModel>, newColumnWeights: List<Float>? = null) {
        header = newHeaders
        data = newRows
        columnWeights = newColumnWeights ?: columnWeights
        notifyDataSetChanged() // RecyclerView 새로고침
    }



}
