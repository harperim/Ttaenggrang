package com.ladysparks.ttaenggrang.ui.revenue

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.data.model.response.TaxNationHistoryResponse
import com.ladysparks.ttaenggrang.databinding.ItemTaxTeacherFragmentNationHistoryBinding
import com.ladysparks.ttaenggrang.util.NumberUtil.formatWithComma

class RevenueTeacherNationHistoryAdapter (
    private var taxNationList: List<TaxNationHistoryResponse>,
) : RecyclerView.Adapter<RevenueTeacherNationHistoryAdapter.TaxViewHolder>() {

    inner class TaxViewHolder(private val binding: ItemTaxTeacherFragmentNationHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tax: TaxNationHistoryResponse) {
            binding.textTitleTeacherNationhistory.text = tax.name
            binding.textAmountTeacherNationhistory.text = formatWithComma(tax.amount)// 100,000 형식 변환
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaxViewHolder {
        val binding = ItemTaxTeacherFragmentNationHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaxViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaxViewHolder, position: Int) {
        holder.bind(taxNationList[position])
    }

    override fun getItemCount(): Int = taxNationList.size

    fun updateData(newList: List<TaxNationHistoryResponse>) {
        taxNationList = newList
        notifyDataSetChanged()  // 전체 데이터 갱신
    }
}