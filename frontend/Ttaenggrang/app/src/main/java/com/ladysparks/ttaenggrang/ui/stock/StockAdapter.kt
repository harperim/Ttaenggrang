package com.ladysparks.ttaenggrang.ui.stock

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi

import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.R

import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.data.model.dto.StockHistoryDto
import com.ladysparks.ttaenggrang.ui.component.LineChartComponent
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StockAdapter(
    private var list: List<StockDto>,
    private val listener: OnStockClickListener
) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    private var selectedPosition = 0
    private var stockHistoryMap: Map<Int, List<StockHistoryDto>> = emptyMap()

    inner class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.textStockName)
        private val price = itemView.findViewById<TextView>(R.id.textStockPrice)
        private val changeRate = itemView.findViewById<TextView>(R.id.textStockChangeRate)
        val miniChart: LineChartComponent = itemView.findViewById(R.id.stockMiniChart)

        fun bind(item: StockDto, isSelected: Boolean) {
            title.text = item.name.substringBefore(" ")
            price.text = item.pricePerShare.toString()
            changeRate.text = "${item.changeRate}%"

//            // ✅ 선택된 아이템 스타일 변경
//            itemView.setBackgroundResource(
//                if (isSelected) R.drawable.bg_selected_stock else R.drawable.bg_default_stock
//            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_stock_list, parent, false)
        return StockViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(
        holder: StockViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val stock = list[position]
        holder.bind(stock, position == selectedPosition)

        // 클릭한 주식 정보 전달
        holder.itemView.setOnClickListener {
            selectedPosition = position
            listener.onStockClick(stock)
            notifyDataSetChanged()
        }

        // ✅ stockId를 기준으로 히스토리 데이터 가져오기
        val filteredStockData = stockHistoryMap[stock.id] ?: emptyList()

        if (filteredStockData.isNotEmpty()) {
            // ✅ 최근 5일치 데이터 가져오기
            val last5DaysStockData = filteredStockData.takeLast(5)

            // ✅ X축 없이도 호환되도록 Pair<Float, Float> 형태 유지 (인덱스, 가격)
            val stockHistory = last5DaysStockData.mapIndexed { index, data ->
                Pair(index.toFloat(), data.price)
            }

            // ✅ 미니 차트 설정
            holder.miniChart.setUpMiniChart(stockHistory)
        }
    }


    override fun getItemCount(): Int = list.size

    // ✅ RecyclerView가 주식 히스토리 데이터를 업데이트할 수 있도록 추가
    fun updateStockHistoryData(newStockHistory: List<StockHistoryDto>) {
        stockHistoryMap = newStockHistory.groupBy { it.id } // stockId 기준으로 그룹화
        notifyDataSetChanged() // UI 갱신
    }

    fun updateData(newList: List<StockDto>) {
        list = newList
        notifyDataSetChanged()
    }
}

interface OnStockClickListener {
    fun onStockClick(stock: StockDto)
}
