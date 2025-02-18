package com.ladysparks.ttaenggrang.ui.revenue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.data.model.response.TaxStudentHistoryResponse
import com.ladysparks.ttaenggrang.databinding.ItemTaxStudentThisMonthBinding
import com.ladysparks.ttaenggrang.util.NumberUtil.formatWithComma

class RevenueStudentMyHistoryAdapter (
    private var taxList: List<TaxStudentHistoryResponse>
): RecyclerView.Adapter<RevenueStudentMyHistoryAdapter.TaxViewHolder>(){

    inner class TaxViewHolder(private val binding: ItemTaxStudentThisMonthBinding) :
    RecyclerView.ViewHolder(binding.root){
        fun bind(tax: TaxStudentHistoryResponse) {
            binding.textDateTaxThisMonth.text = tax.paymentDate
            binding.textRateTaxThisMonth.text = "${ tax.taxRate * 100}%"
            binding.textTitleTaxThisMonth.text = tax.taxName
            binding.textAmountTaxThisMonth.text = formatWithComma(tax.amount)
            binding.textDescriptionTaxThisMonth.text = tax.taxDescription
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaxViewHolder {
        val binding = ItemTaxStudentThisMonthBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaxViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaxViewHolder, position: Int) {
        holder.bind(taxList[position])
    }

    override fun getItemCount(): Int = taxList.size

    fun updateData(newList: List<TaxStudentHistoryResponse>) {
        taxList = newList
        notifyDataSetChanged()  // 전체 데이터 갱신
    }

}