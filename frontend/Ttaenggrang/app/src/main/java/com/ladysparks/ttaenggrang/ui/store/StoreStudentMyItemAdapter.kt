package com.ladysparks.ttaenggrang.ui.store

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.data.model.response.StoreStudentPurchaseHistoryResponse

class StoreStudentMyItemAdapter (
    private var myItemList: List<StoreStudentPurchaseHistoryResponse>,
    private val onMyItemClick: (StoreStudentPurchaseHistoryResponse) -> Unit
): RecyclerView.Adapter<StoreStudentMyItemAdapter.StoreStudentMyItemViewHolder>() {

    inner class StoreStudentMyItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemPrice = itemView.findViewById<TextView>(R.id.textMyItemPrice)
        private val itemName = itemView.findViewById<TextView>(R.id.textMyItemName)

        // 상품 가격과 상품명이 보임
        fun bind(item: StoreStudentPurchaseHistoryResponse) {
            itemPrice.text = "${item.itemPrice}"
            itemName.text = item.itemName

            // 클릭하면 상세 다이얼로그로 감 - 여기서 사용할 수 있음
            itemView.setOnClickListener{
                Log.d("ItemClickTest", "아이템 클릭됨: ${item.itemName}")
                onMyItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoreStudentMyItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_item, parent, false)
        return StoreStudentMyItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreStudentMyItemViewHolder, position: Int) {
        holder.bind(myItemList[position])
    }

    override fun getItemCount(): Int {
        return myItemList.size
    }

    fun updateData(newList: List<StoreStudentPurchaseHistoryResponse>) {
        myItemList = newList
        Log.d("AdapterDataTest", "어댑터에 데이터 업데이트됨: ${newList.size} 개 아이템")
        notifyDataSetChanged()
    }
}