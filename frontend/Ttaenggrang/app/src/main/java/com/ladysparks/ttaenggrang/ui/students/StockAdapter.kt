package com.ladysparks.ttaenggrang.ui.students

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.data.model.response.StockResponse
import com.ladysparks.ttaenggrang.databinding.ItemStockBinding
import com.ladysparks.ttaenggrang.util.NumberUtil

class StockAdapter(private var stockList: List<StockResponse>) :
    RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    inner class StockViewHolder(val binding: ItemStockBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = ItemStockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val stock = stockList[position]

        with(holder.binding) {
            var currentPrice = (stock.currentTotalPrice / stock.quantity) //현재 주식 가격

            textStockName.text = stock.stockName
            textStockQuantity.text = "${stock.quantity}주"
            textStockBuyPrice.text = "주당 구매가격 ${NumberUtil.formatWithComma(stock.purchasePrice)}"
            textStockTotalValue.text = NumberUtil.formatWithComma(stock.currentTotalPrice)

            // 변동률
            val stockChangeValue = (((currentPrice.toDouble() - stock.purchasePrice.toDouble()) / stock.purchasePrice.toDouble()) * 100)
            val formattedChangeValue = String.format("%.1f", stockChangeValue)

            if(stockChangeValue > 0) {
                textStockChangeValue.text = "▲ $formattedChangeValue%"
                textStockChangeValue.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
                imageStockChangeIcon.setImageResource(R.drawable.ic_chevron_up)
            }else if ( stockChangeValue < 0){
                textStockChangeValue.text = "▼ $formattedChangeValue%"
                textStockChangeValue.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.blue))
                imageStockChangeIcon.setImageResource(R.drawable.ic_chevron_down)
            }else{
                textStockChangeValue.text = "0"
                textStockChangeValue.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.lightGray))
                imageStockChangeIcon.visibility = View.GONE
            }



            Log.d("TAG", "onBindViewHolder: 가격 변동 체크 ${currentPrice} . ${stock.purchasePrice} , ${stockChangeValue}")
            textStockChangeValue.text = "$formattedChangeValue %"
//            textStockPrice.text = "현재 가격: ${NumberUtil.formatWithComma(stock.purchasePrice)}"
        }
    }

    override fun getItemCount(): Int = stockList.size

    fun updateData(newStockList: List<StockResponse>) {
        stockList = newStockList
        notifyDataSetChanged()
    }
}
