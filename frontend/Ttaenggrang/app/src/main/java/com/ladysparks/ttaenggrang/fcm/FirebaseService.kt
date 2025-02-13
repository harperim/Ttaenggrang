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

        // 알람 메시지의 경우, data 페이로드 값이 null 인상태로 넘어온다.
        // 우선 data로 데이터값을 분리 한후 이후 로직 작성
        if (remoteMessage.notification != null) {
            // notification이 있는 경우 foreground처리
            //foreground
            messageTitle = remoteMessage.notification!!.title.toString()
            messageContent = remoteMessage.notification!!.body.toString()
        } else {
            // background 에 있을경우 혹은 foreground에 있을경우 두 경우 모두
            val data = remoteMessage.data
            messageTitle = data.get("myTitle").toString()
            messageContent = data.get("myBody").toString()
        }

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val mainPendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE)

        val builder1 = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(messageTitle)
            .setContentText(messageContent)
            .setAutoCancel(true)
            .setContentIntent(mainPendingIntent)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(101, builder1.build())

    }

}
