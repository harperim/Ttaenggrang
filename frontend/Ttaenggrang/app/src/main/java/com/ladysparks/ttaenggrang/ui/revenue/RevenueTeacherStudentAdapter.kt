package com.ladysparks.ttaenggrang.ui.revenue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.data.model.response.TaxStudentTotalResponse
import com.ladysparks.ttaenggrang.databinding.ItemTaxTeacherFragmentStudentHistoryBinding
import com.ladysparks.ttaenggrang.util.NumberUtil.formatWithComma

class RevenueTeacherStudentAdapter (
    private var taxList: List<TaxStudentTotalResponse>,
    private val onItemClick: (studentId: Int) -> Unit
) : RecyclerView.Adapter<RevenueTeacherStudentAdapter.TaxViewHolder>() {

    inner class TaxViewHolder(private val binding: ItemTaxTeacherFragmentStudentHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tax: TaxStudentTotalResponse) {
            binding.textStudentNameTaxHistory.text = tax.studentName
            binding.textStudentAmountTaxHistory.text = formatWithComma(tax.totalAmount) // 100,000 형식 변환

            binding.root.setOnClickListener {
                onItemClick(tax.studentId) // 클릭 시 다이얼로그 열기
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaxViewHolder {
        val binding = ItemTaxTeacherFragmentStudentHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaxViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaxViewHolder, position: Int) {
        holder.bind(taxList[position])
    }

    override fun getItemCount(): Int = taxList.size

    fun updateData(newList: List<TaxStudentTotalResponse>) {
        taxList = newList
        notifyDataSetChanged()  // 전체 데이터 갱신
    }
}