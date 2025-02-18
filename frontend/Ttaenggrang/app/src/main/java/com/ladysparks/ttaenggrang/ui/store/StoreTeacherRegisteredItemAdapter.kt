package com.ladysparks.ttaenggrang.ui.store

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.data.model.response.StoreStudenItemListResponse
import com.ladysparks.ttaenggrang.util.NumberUtil.formatWithComma


class StoreTeacherRegisteredItemAdapter (
    private var itemList: List<StoreStudenItemListResponse>,
): RecyclerView.Adapter<StoreTeacherRegisteredItemAdapter.StoreTeacherRegisteredItemViewHolder>(){

    inner class StoreTeacherRegisteredItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textItemNumber = itemView.findViewById<TextView>(R.id.textItemNumber)
        private val textItemName = itemView.findViewById<TextView>(R.id.textItemName)
        private val textItemDescription = itemView.findViewById<TextView>(R.id.textItemDescription)
        private val textItemPrice = itemView.findViewById<TextView>(R.id.textItemPrice)
        private val textItemQuantity = itemView.findViewById<TextView>(R.id.textItemQuantity)

        fun bind(item: StoreStudenItemListResponse, position: Int) {
            textItemNumber.text = "${position + 1}"
            textItemName.text = item.name
            textItemDescription.text = item.description
            textItemPrice.text = "${formatWithComma(item.price)}원"
            textItemQuantity.text = "${item.quantity}개"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoreTeacherRegisteredItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_registered_item, parent, false)
        return StoreTeacherRegisteredItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreTeacherRegisteredItemViewHolder, position: Int) {
        holder.bind(itemList[position], position)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateData(newList: List<StoreStudenItemListResponse>) {
        itemList = newList
        Log.d("AdapterDataTest", "어댑터에 데이터 업데이트됨: ${newList.size} 개 아이템")
        notifyDataSetChanged()
    }

}