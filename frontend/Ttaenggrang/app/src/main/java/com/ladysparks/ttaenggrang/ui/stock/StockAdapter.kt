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

        fun bind(item: StockDto, isSelected: Boolean, stockHistory: List<StockHistoryDto>?) {
            title.text = item.name.substringBefore(" ")
            price.text = item.pricePerShare.toString()
            changeRate.text = "${item.changeRate}%"

            // ✅ 미니 차트 데이터 설정
            if (!stockHistory.isNullOrEmpty()) {
                val last5DaysStockData = stockHistory.takeLast(5)

                // ✅ X축 없이도 호환되도록 Pair<Float, Float> 형태 유지 (인덱스, 가격)
                val stockHistoryData = last5DaysStockData.mapIndexed { index, data ->
                    Pair(index.toFloat(), data.price)
                }

                // ✅ 미니 차트 설정
                miniChart.setUpMiniChart(stockHistoryData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_stock_list, parent, false)
        return StockViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: StockViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val stock = list[position]
        val stockHistory = stockHistoryMap[stock.id] // ✅ stockId 기준으로 히스토리 가져오기

        // ✅ `bind()` 함수에서 미니 차트 포함하여 호출
        holder.bind(stock, position == selectedPosition, stockHistory)

        // 클릭한 주식 정보 전달
        holder.itemView.setOnClickListener {
            selectedPosition = position
            listener.onStockClick(stock)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = list.size

    // ✅ RecyclerView가 주식 히스토리 데이터를 업데이트할 수 있도록 추가
    fun updateStockHistoryData(newStockHistory: List<StockHistoryDto>) {
        stockHistoryMap = newStockHistory.groupBy { it.stockId } // stockId 기준으로 그룹화
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
