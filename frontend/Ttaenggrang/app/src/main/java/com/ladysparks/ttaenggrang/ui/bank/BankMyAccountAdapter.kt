package com.ladysparks.ttaenggrang.ui.bank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.data.model.dto.BankHistoryDto
import com.ladysparks.ttaenggrang.data.model.dto.BankManageDto
import com.ladysparks.ttaenggrang.util.NumberUtil

class BankMyAccountAdapter(
    private var accountList:List<Pair<BankManageDto, BankHistoryDto?>>,
    private val onItemClick: (Pair<BankManageDto, BankHistoryDto?>) -> Unit // 클릭 이벤트
) : RecyclerView.Adapter<BankMyAccountAdapter.BankMyAccountItemViewHolder>() {

    inner class BankMyAccountItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val accountName = itemView.findViewById<TextView>(R.id.textAccountName)
        private val depositAmount = itemView.findViewById<TextView>(R.id.textAccountMoney)


        fun bind(item: Pair<BankManageDto, BankHistoryDto?>) {
            val bankManage = item.first
            val bankHistory = item.second

            accountName.text = bankHistory?.savingsName ?: "알 수 없음"
            depositAmount.text = NumberUtil.formatWithComma("${bankManage.depositAmount}")

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankMyAccountItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_myaccount_list, parent, false)
        return BankMyAccountItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: BankMyAccountItemViewHolder, position: Int) {
        holder.bind(accountList[position])
    }

    override fun getItemCount(): Int = accountList.size

    fun updateData(newList: List<Pair<BankManageDto, BankHistoryDto?>>) {
        accountList = newList
        notifyDataSetChanged()
    }

}