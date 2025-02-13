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
import com.ladysparks.ttaenggrang.data.dummy.StockDummyData
import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.ui.component.LineChartComponent

class StockAdapter(
    private var list: List<StockDto>,
    private val listener: OnStockClickListener
) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    private var selectedPosition = 0

    inner class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.textStockName)
        private val price = itemView.findViewById<TextView>(R.id.textStockPrice)
        private val changeRate = itemView.findViewById<TextView>(R.id.textStockChangeRate)
        val miniChart: LineChartComponent = itemView.findViewById(R.id.stockMiniChart)

        fun bind(item: StockDto, isSelected: Boolean) {
            title.text = item.name.substringBefore(" ")
            price.text = item.pricePer.toString()
            changeRate.text = "${item.changeRate}%"

//            // ✅ 선택된 아이템 스타일 변경
//            itemView.setBackgroundResource(
//                if (isSelected) R.drawable.bg_selected_stock else R.drawable.bg_default_stock
//            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stock_list, parent, false)
        return StockViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: StockViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val stock = list[position]
        holder.bind(stock, position == selectedPosition)

        // 클릭한 주식 정보 전달
        holder.itemView.setOnClickListener {
            selectedPosition = position
            listener.onStockClick(stock)
            notifyDataSetChanged()
        }

        // ✅ 더미 데이터 생성 (여기서는 최근 7일 데이터 사용)
        val dummyStockData = StockDummyData.generateStockSampleData(stock.name, stock.id)
        val last7DaysStockData = dummyStockData.takeLast(7)

        val stockHistory = last7DaysStockData.mapIndexed { index, data ->
            Pair(index.toFloat(), data.price)
        }

        // ✅ 미니 차트 설정
        holder.miniChart.setUpMiniChart(stockHistory)
    }


    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<StockDto>) {
        list = newList
        notifyDataSetChanged()
    }
}

interface OnStockClickListener {
    fun onStockClick(stock: StockDto)
}
