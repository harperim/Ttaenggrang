package com.ladysparks.ttaenggrang.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.FirebaseMessagingService
import com.ladysparks.ttaenggrang.MainActivity
import com.ladysparks.ttaenggrang.base.ApplicationClass
import com.ladysparks.ttaenggrang.realm.NotificationModel

class FirebaseService : FirebaseMessagingService(){ //  : FirebaseMessagingService()
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // 새 토큰 수행시 할 작업 수행
        // MainActivity.uploadToken(token) // 새 토큰 수신 시 서버 전송
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d("FCM 메시지 받기", "onMessageReceived: ${remoteMessage.notification!!.title}")
        var messageTitle = ""
        var messageContent = ""
        var category = ""
        var sender = ""
        var receiver = ""
        var time = System.currentTimeMillis()

        // 메시지 수신 (foreground 처리)
        if (remoteMessage.notification != null) {
            messageTitle = remoteMessage.notification!!.title.toString()
            messageContent = remoteMessage.notification!!.body.toString()
        }

        // `data` 필드가 있을 경우 → `NotificationModel` 구조에 맞게 매핑
        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            messageTitle = data["title"] ?: messageTitle
            messageContent = data["message"] ?: messageContent
            category = data["notificationType"] ?: "일반"
            sender = data["sender"] ?: "System"
            receiver = data["receiver"] ?: "" // TEACHER, STUDENT
            time = data["time"]?.toLongOrNull() ?: System.currentTimeMillis()
        }

        saveMessageToRealm(messageTitle, messageContent, category, sender, receiver, time)
        showNotification(messageTitle, messageContent)
    }

    private fun saveMessageToRealm(title: String, content: String, category: String, sender: String, receiver: String, time: Long) {
        val realm = ApplicationClass.realm
        realm.writeBlocking {
            copyToRealm(NotificationModel().apply {
                this.title = title
                this.content = content
                this.category = category
                this.sender = sender
                this.receiver = receiver
                this.time = time
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
