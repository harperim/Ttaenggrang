package com.ladysparks.ttaenggrang.ui.revenue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.data.model.response.TaxStudentHistoryResponse
import com.ladysparks.ttaenggrang.databinding.ItemTaxTeacherStudentHistoryBinding
import com.ladysparks.ttaenggrang.util.NumberUtil.formatWithComma

class StudentTaxHistoryAdapter (
    private var taxHistoryList: List<TaxStudentHistoryResponse>
) : RecyclerView.Adapter<StudentTaxHistoryAdapter.ViewHolder>(){

    inner class ViewHolder(private val binding: ItemTaxTeacherStudentHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tax: TaxStudentHistoryResponse) {
            binding.textDateStudentTaxHistory.text = tax.paymentDate
            binding.textTitleStudentTaxHistory.text = tax.taxName
            binding.textDescriptionStudentTaxHistory.text = "${(tax.taxRate * 100).toInt()}%"
            binding.textAmountStudentTaxHistory.text = formatWithComma(tax.amount)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTaxTeacherStudentHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(taxHistoryList[position])
    }

    override fun getItemCount(): Int = taxHistoryList.size

    fun updateData(newList: List<TaxStudentHistoryResponse>) {
        taxHistoryList = newList
        notifyDataSetChanged()
    }

}