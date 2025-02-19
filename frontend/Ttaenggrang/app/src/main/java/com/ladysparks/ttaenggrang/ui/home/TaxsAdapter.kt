package com.ladysparks.ttaenggrang.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.data.model.dto.TaxDto

class TaxsAdapter(private val taxsList: List<TaxDto>) :
    RecyclerView.Adapter<TaxsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taxName: TextView = view.findViewById(R.id.textTaxName)
        val taxRate: TextView = view.findViewById(R.id.textTaxRate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_taxes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tax = taxsList[position]
        holder.taxName.text = tax.taxName
        holder.taxRate.text = "${(tax.taxRate * 100).toInt()}%" // 소수 → 정수 변환 (5% 형식)
    }

    override fun getItemCount(): Int = taxsList.size
}
