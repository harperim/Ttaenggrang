package com.ladysparks.ttaenggrang.ui.bank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.data.model.response.StoreStudenItemListResponse
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.ui.store.StoreStudentAdapter

class BankAdapter (
    private var storeItemList: List<StoreStudenItemListResponse>,
    private val onItemClick: (StoreStudenItemListResponse) -> Unit // 클릭 이벤트
): RecyclerView.Adapter<StoreStudentAdapter.StoreItemViewHolder>() {

    inner class StoreItemViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        private val itemName = itemView.findViewById<TextView>(R.id.textItemName)
        private val itemPrice = itemView.findViewById<TextView>(R.id.textItemPrice)
//        private  val itemImage

        fun bind(item: StoreStudenItemListResponse) {
            itemName.text = item.name
            itemPrice.text = "${item.price}"
//            itemImage

            itemView.setOnClickListener{
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shop, parent, false)
        return StoreItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreItemViewHolder, position: Int) {
        holder.bind(storeItemList[position])
    }

    override fun getItemCount(): Int  = storeItemList.size

    fun updateData(newList: List<StoreStudenItemListResponse>) {
        storeItemList = newList
        notifyDataSetChanged()
    }

}