package com.ladysparks.ttaenggrang.ui.revenue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.data.model.response.TaxTeacherInfoResponse

class RevenueTeacherInfoAdapter(
    private var taxList: List<TaxTeacherInfoResponse>
) : RecyclerView.Adapter<RevenueTeacherInfoAdapter.TaxInfoViewHolder>() {

    // 세금 정보 리스트
    inner class TaxInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTitleTaxInfo: TextView = itemView.findViewById(R.id.textTitleTaxInfo)
        private val textDescriptionTaxInfo: TextView = itemView.findViewById(R.id.textDescriptionTaxInfo)
        private val textRateTaxInfo: TextView = itemView.findViewById(R.id.textRateTaxInfo)

        fun bind(taxInfo: TaxTeacherInfoResponse) {
            textTitleTaxInfo.text = taxInfo.taxName
            textDescriptionTaxInfo.text = taxInfo.taxDescription ?: ""
            textRateTaxInfo.text = "${(taxInfo.taxRate * 100).toInt()}%"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaxInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tax_teacher_info, parent, false)
        return TaxInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaxInfoViewHolder, position: Int) {
        holder.bind(taxList[position])
    }

    override fun getItemCount(): Int = taxList.size

    // 새로운 리스트로 데이터를 업데이트하는 함수
    fun updateData(newList: List<TaxTeacherInfoResponse>) {
        taxList = newList
        notifyDataSetChanged()
    }

}