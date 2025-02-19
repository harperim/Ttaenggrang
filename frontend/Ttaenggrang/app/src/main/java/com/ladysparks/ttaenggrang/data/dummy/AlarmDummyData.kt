package com.ladysparks.ttaenggrang.data.dummy

import android.util.Log
import com.ladysparks.ttaenggrang.base.ApplicationClass
import com.ladysparks.ttaenggrang.realm.NotificationModel

class AlarmDummyData {

    private fun insertSampleNotifications() {
        val realm = ApplicationClass.realm
        realm.writeBlocking {
            val sampleNotifications = listOf(
                NotificationModel().apply {
                    title = "만기 알림"
                    content = "만기 예정 적금이 존재합니다."
                    category = "BANK"
                    sender = "SYSTEM"
                    receiver = "TEACHER"
                    sendTime = System.currentTimeMillis().toString()
                },
                NotificationModel().apply {
                    title = "주간 보고서 발행"
                    content = "이번주 주간 보고서가 발행되었습니다."
                    category = "REPORT"
                    sender = "SYSTEM"
                    receiver = "TEACHER"
                    sendTime = System.currentTimeMillis().toString()
                },
                NotificationModel().apply {
                    title = "오늘의 뉴스"
                    content = "뉴스 정보를 확인하고 주식 투자를 진행하세요"
                    category = "NEWS"
                    sender = "SYSTEM"
                    receiver = "TEACHER"
                    sendTime = System.currentTimeMillis().toString()
                }

            )

            sampleNotifications.forEach { copyToRealm(it) }
        }
        Log.d("Realm", "샘플 FCM 메시지 저장 완료")
    }

}