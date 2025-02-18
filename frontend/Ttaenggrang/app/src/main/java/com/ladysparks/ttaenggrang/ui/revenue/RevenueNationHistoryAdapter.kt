package com.ladysparks.ttaenggrang.ui.revenue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.data.model.response.TaxNationHistoryResponse
import com.ladysparks.ttaenggrang.util.NumberUtil.formatWithComma

class RevenueNationHistoryAdapter(
    private var taxUseList: List<TaxNationHistoryResponse>
) : RecyclerView.Adapter<RevenueNationHistoryAdapter.TaxInfoViewHolder>() {

    // 세금 정보 리스트
    inner class TaxInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textDateNationTax: TextView = itemView.findViewById(R.id.textDateNationTax)
        private val textTitleNationTax: TextView = itemView.findViewById(R.id.textTitleNationTax)
        private val textDescriptionNationTax: TextView = itemView.findViewById(R.id.textDescriptionNationTax)
        private val textAmountNationTax: TextView = itemView.findViewById(R.id.textAmountNationTax)

        fun bind(taxInfo: TaxNationHistoryResponse) {
            textDateNationTax.text = taxInfo.usageDate
            textTitleNationTax.text = taxInfo.name
            textDescriptionNationTax.text = taxInfo.description
            textAmountNationTax.text = formatWithComma(taxInfo.amount)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaxInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tax_student_nation, parent, false)
        return TaxInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaxInfoViewHolder, position: Int) {
        holder.bind(taxUseList[position])
    }

    override fun getItemCount(): Int = taxUseList.size

    // 새로운 리스트로 데이터를 업데이트하는 함수
    fun updateData(newList: List<TaxNationHistoryResponse>) {
        taxUseList = newList
        notifyDataSetChanged()
    }

}