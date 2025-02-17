package com.ladysparks.ttaenggrang.base

import RoundedBackgroundSpan
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel

class BaseTableStyleAdapter(
    private var header: List<String>,
    private var data: List<BaseTableRowModel>,
    private var columnWeights: List<Float> = List(header.size) { 1f },
    private val onRowClickListener: ((rowIndex: Int, rowData: List<String>) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

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

    override fun getItemCount(): Int = data.size + 1

    class HeaderViewHolder(itemView: View, private val columnWeights: List<Float>) :
        RecyclerView.ViewHolder(itemView) {
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
                    layoutParams =
                        LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight)
                }
                container.addView(textView)
            }
        }
    }

    class ItemViewHolder(
        itemView: View,
        private val columnWeights: List<Float>,
        private val onRowClickListener: ((rowIndex: Int, rowData: List<String>) -> Unit)?
    ) : RecyclerView.ViewHolder(itemView) {
        private val container: LinearLayout = itemView.findViewById(R.id.rowContainer)

        @SuppressLint("ResourceAsColor")
        fun bind(rowData: List<String>, rowIndex: Int) {
            container.removeAllViews()
            for ((index, text) in rowData.withIndex()) {
                val weight = columnWeights.getOrElse(index) { 1f }

                // ✅ FrameLayout을 사용하여 컬럼 크기와 상관없이 배경을 조정
                val frameLayout = FrameLayout(itemView.context).apply {
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight)
                }

                val textView = TextView(itemView.context).apply {
                    this.text = text
                    setPadding(20, 10, 20, 10) // ✅ 텍스트 길이에 맞는 패딩 설정
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, // ✅ 배경이 텍스트 크기에 맞게 적용
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.CENTER // ✅ 여기에서 Gravity 설정
                    }

                    // ✅ 5번째 컬럼(index == 4)에서 "호재"와 "악재"만 스타일 적용
                    if (index == 4) {
                        when (text) {
                            "호재" -> {
                                setBackgroundResource(R.drawable.bg_positive) // 초록색 둥근 배경
                                setTextColor(ContextCompat.getColor(context, R.color.positive_green))
                                setTypeface(null, Typeface.BOLD)
                            }
                            "악재" -> {
                                setBackgroundResource(R.drawable.bg_negative) // 빨간색 둥근 배경
                                setTextColor(ContextCompat.getColor(context, R.color.negative_red))
                                setTypeface(null, Typeface.BOLD)
                            }
                            else -> {
                                setBackgroundResource(0) // 기본 배경 제거
                                setTextColor(Color.BLACK)
                            }
                        }
                    }
                }

                frameLayout.addView(textView)
                container.addView(frameLayout)
            }

            itemView.setOnClickListener {
                onRowClickListener?.invoke(rowIndex, rowData)
            }
        }

    }

    fun updateData(newRows: List<BaseTableRowModel>) {
        data = newRows
        notifyDataSetChanged()
    }

    fun updateData(
        newHeaders: List<String>,
        newRows: List<BaseTableRowModel>,
        newColumnWeights: List<Float>? = null
    ) {
        header = newHeaders
        data = newRows
        columnWeights = newColumnWeights ?: columnWeights
        notifyDataSetChanged()
    }
}
