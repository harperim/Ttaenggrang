package com.ladysparks.ttaenggrang.ui.home

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.decode.GifDecoder
import coil.load
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.realm.NotificationModel
import com.ladysparks.ttaenggrang.util.CustomDateUtil
import com.ladysparks.ttaenggrang.util.NavigationManager
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_BANK_STUDENT
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_BANK_TEACHER
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_STOCK_STUDENT
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_STOCK_TEACHER
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil

class AlarmAdapter(
    private var alarmList: List<NotificationModel>,
    private val context: Context // 컨텍스트 추가 (다이얼로그 띄우기 위해 필요)
) : RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {

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
        holder.content.text = item.content.takeIf { it.isNotEmpty() } ?: "-----------"
        holder.sender.text = item.sender
        holder.time.text = CustomDateUtil.formatToDate(item.sendTime)

        // 클릭 시 상세 다이얼로그 표시
        holder.itemView.setOnClickListener {
            showDetailDialog(item)
        }
    }

    override fun getItemCount(): Int = alarmList.size

    // 데이터를 업데이트하는 함수 (Realm에서 새 데이터를 불러올 때 사용)
    fun updateData(newList: List<NotificationModel>) {
        alarmList = newList
        notifyDataSetChanged()
    }

    // 다이얼로그 표시 함수
    private fun showDetailDialog(notification: NotificationModel) {
        val dialog = Dialog(context)

        // 다이얼로그 레이아웃 설정 (XML 적용)
        dialog.setContentView(R.layout.dialog_alarm_detail)

        // 다이얼로그 내부 View 찾기
        val image: ImageView = dialog.findViewById(R.id.imgDialogStatus)
        val contentText: TextView = dialog.findViewById(R.id.textDialogTitle)
        val senderText: TextView = dialog.findViewById(R.id.textDialogContent)
        val btnConfirm: TextView = dialog.findViewById(R.id.btnDialogConfirm)

        // 카테고리별 GIF 적용
        loadCategoryImage(image, notification.category)

        // 학생/선생 Type 별 페이지 이동 적용
        setClickEvent(dialog, btnConfirm, notification.category, SharedPreferencesUtil.getValue(SharedPreferencesUtil.IS_TEACHER, false))

        // 내용 표시
        contentText.text = notification.title
        senderText.text = "${notification.content}"

        dialog.show()
    }


    private fun loadCategoryImage(image: ImageView, category: String) {
        val imageRes = when (category) {
            "BANK" -> R.raw.dollar
            "REPORT" -> R.raw.report
            "NEWS" -> R.raw.news
            else -> R.raw.dollar
        }

        image.load(Uri.parse("android.resource://${context.packageName}/$imageRes")) {
            crossfade(true)
            decoderFactory(GifDecoder.Factory())
        }
    }

    private fun setClickEvent(
        dialog: Dialog,
        btnConfirm: TextView,
        category: String,
        is_teacher: Boolean?
    ) {
        btnConfirm.setOnClickListener {
            val targetPage = when {
                // BANK
                is_teacher == true && category == "BANK" -> {
                    btnConfirm.text = "이동"
                    NavigationManager.moveFragment(FRAGMENT_BANK_TEACHER)
                    dialog.dismiss()
                }
                is_teacher == false && category == "BANK" -> {
                    btnConfirm.text = "이동"
                    NavigationManager.moveFragment(FRAGMENT_BANK_STUDENT)
                    dialog.dismiss()
                }

                // NEWS
                is_teacher == true && category == "NEWS" -> {
                    btnConfirm.text = "이동"
                    NavigationManager.moveFragment(FRAGMENT_STOCK_TEACHER)
                    dialog.dismiss()
                }
                is_teacher == false && category == "NEWS" -> {
                    btnConfirm.text = "이동"
                    NavigationManager.moveFragment(FRAGMENT_STOCK_STUDENT)
                    dialog.dismiss()
                }
                else -> null
            }

            if(targetPage == null){
                dialog.dismiss()
            }

        }
    }
}
