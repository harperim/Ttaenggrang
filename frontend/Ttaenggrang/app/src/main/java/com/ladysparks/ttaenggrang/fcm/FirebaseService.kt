package com.ladysparks.ttaenggrang.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.FirebaseMessagingService
import com.ladysparks.ttaenggrang.MainActivity
import com.ladysparks.ttaenggrang.base.ApplicationClass
import com.ladysparks.ttaenggrang.realm.NotificationModel
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil

class FirebaseService : FirebaseMessagingService(){ //  : FirebaseMessagingService()
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // 새 토큰 수행시 할 작업 수행
        // MainActivity.uploadToken(token) // 새 토큰 수신 시 서버 전송
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d("FCM...", "${remoteMessage.data}")

        var messageTitle = ""
        var messageContent = ""
        var category = ""
        var sender = ""
        var receiver = ""
        var sendTime =""

        // 메시지 수신 (foreground 처리)
        if (remoteMessage.notification != null) {
            messageTitle = remoteMessage.notification!!.title.toString()
            messageContent = remoteMessage.notification!!.body.toString()
        }

        // `data` 필드가 있을 경우 → `NotificationModel` 구조에 맞게 매핑
        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            category = data["category"] ?: "일반"
            messageTitle = data["title"] ?: messageTitle
            messageContent = data["content"] ?: messageContent
            sender = data["sender"] ?: "System"
            receiver = data["receiver"] ?: "" // TEACHER, STUDENT
            sendTime = data["time"]?.toLongOrNull().toString() ?: System.currentTimeMillis().toString()
        }

        if(category.equals("BANK")){
            SharedPreferencesUtil.putValue(SharedPreferencesUtil.IS_EVENT_SHOW, true)
        }

        saveMessageToRealm(messageTitle, messageContent, category, sender, receiver, sendTime) // db 넣기
        showNotification(messageTitle, messageContent) // noti alarm 생성

        // MainActivity 로 메시지 전달
        val intent = Intent("FCM_MESSAGE_RECEIVED")
        intent.putExtra("title", messageTitle)
        intent.putExtra("content", messageContent)
        intent.putExtra("category", category)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun saveMessageToRealm(title: String, content: String, category: String, sender: String, receiver: String, time: String) {
        val realm = ApplicationClass.realm
        realm.writeBlocking {
            copyToRealm(NotificationModel().apply {
                this.title = title
                this.content = content
                this.category = category
                this.sender = sender
                this.receiver = receiver
                this.sendTime = time
            })
        }
        Log.d("FCM", "Realm에 FCM 메시지 저장 완료: $title")
    }

    private fun showNotification(title: String, content: String){
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val mainPendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE)

        val builder1 = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setContentIntent(mainPendingIntent)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(101, builder1.build())
    }

}
