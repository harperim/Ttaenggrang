package com.ladysparks.ttaenggrang.ui.home

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.data.model.dto.AlarmDto
import com.ladysparks.ttaenggrang.realm.NotificationModel
import com.ladysparks.ttaenggrang.util.DataUtil

class AlarmAdapter(private var alarmList: List<NotificationModel>) :
    RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textAlarmTitle)
        val content: TextView = view.findViewById(R.id.textAlarmContent)
        val sender: TextView = view.findViewById(R.id.textAlarmPublish)
        val time: TextView = view.findViewById(R.id.textAlarmDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = alarmList[position]
        holder.title.text = item.title
        holder.content.text = item.content
        holder.sender.text = item.sender
        holder.time.text = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", item.time)
    }

    override fun getItemCount(): Int = alarmList.size

    // 🔹 데이터를 업데이트하는 함수 (Realm에서 새 데이터를 불러올 때 사용)
    fun updateData(newList: List<NotificationModel>) {
        alarmList = newList
        notifyDataSetChanged()
    }

}
