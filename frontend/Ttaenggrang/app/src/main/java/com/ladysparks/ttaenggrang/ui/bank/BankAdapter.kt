package com.ladysparks.ttaenggrang.ui.bank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.data.model.dto.BankItemDto
import com.ladysparks.ttaenggrang.util.NumberUtil

class BankAdapter (
    private var bankItemList: List<BankItemDto>,
    private val onItemClick: (BankItemDto) -> Unit // 클릭 이벤트
): RecyclerView.Adapter<BankAdapter.BankItemViewHolder>() {

    inner class BankItemViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        private val itemName = itemView.findViewById<TextView>(R.id.textItemTitle)
        private val duration = itemView.findViewById<TextView>(R.id.textDuration)
        private val interestRate = itemView.findViewById<TextView>(R.id.textinterestRate)
        private val amount = itemView.findViewById<TextView>(R.id.textAmount)

        fun bind(item: BankItemDto) {
            itemName.text = item.name
            duration.text = "${item.durationWeeks} 주"
            interestRate.text = "${item.interestRate}%"
            amount.text = NumberUtil.formatWithComma(item.amount.toString())

            itemView.setOnClickListener{
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bank_product, parent, false)
        return BankItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: BankItemViewHolder, position: Int) {
        holder.bind(bankItemList[position])
    }

    override fun getItemCount(): Int  = bankItemList.size

    fun updateData(newList: List<BankItemDto?>) {
        bankItemList = newList as List<BankItemDto>
        notifyDataSetChanged()
    }

}