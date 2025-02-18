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
                    title = "긴급 공지"
                    content = "내일 등교 시간이 변경되었습니다."
                    category = "Report"
                    sender = "학교 관리자"
                    receiver = "STUDENT"
                    time = System.currentTimeMillis()
                },
                NotificationModel().apply {
                    title = "새로운 과제"
                    content = "국어 숙제를 확인하세요."
                    category = "OTHER"
                    sender = "국어 선생님"
                    receiver = "STUDENT"
                    time = System.currentTimeMillis()
                },
                NotificationModel().apply {
                    title = "학급 회의"
                    content = "오늘 오후 3시 학급 회의가 있습니다."
                    category = "Report"
                    sender = "반장"
                    receiver = "TEACHER"
                    time = System.currentTimeMillis()
                }
            )

            sampleNotifications.forEach { copyToRealm(it) }
        }
        Log.d("Realm", "샘플 FCM 메시지 저장 완료")
    }

}