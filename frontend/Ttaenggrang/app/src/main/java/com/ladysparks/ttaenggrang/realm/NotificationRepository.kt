package com.ladysparks.ttaenggrang.realm

import com.ladysparks.ttaenggrang.base.ApplicationClass
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults

object NotificationRepository {
    /**  Realm에서 모든 알림 가져오기 */
    fun getAllNotifications(): List<NotificationModel> {
        val realm = ApplicationClass.realm
        return realm.query<NotificationModel>()
            .sort("receivedTime", io.realm.kotlin.query.Sort.DESCENDING)
            .find()
    }
    /** "TEACHER" 수신자만 필터링하여 알림 가져오기 (최신순 정렬) */
    fun getTeacherNotifications(): List<NotificationModel> {
        val realm = ApplicationClass.realm
        return realm.query<NotificationModel>("receiver == $0", "TEACHER")
            .sort("receivedTime", io.realm.kotlin.query.Sort.DESCENDING)
            .find()
    }

    /** "STUDENT" 수신자만 필터링하여 알림 가져오기 (최신순 정렬) */
    fun getStudentNotifications(): List<NotificationModel> {
        val realm = ApplicationClass.realm
        return realm.query<NotificationModel>("receiver == $0", "STUDENT")
            .sort("receivedTime", io.realm.kotlin.query.Sort.DESCENDING)
            .find()
    }

    /** 새로운 알림 추가 */
    fun addNotification(notification: NotificationModel) {
        val realm = ApplicationClass.realm
        realm.writeBlocking {
            // receivedTime을 현재 시간으로 설정 후 저장
            notification.receivedTime = System.currentTimeMillis()
            copyToRealm(notification, updatePolicy = io.realm.kotlin.UpdatePolicy.ALL)
        }
    }


    /** 특정 알림 삭제 */
    fun deleteNotification(notificationId: String) {
        val realm = ApplicationClass.realm
        realm.writeBlocking {
            val notification = query<NotificationModel>("id == $0", notificationId).first().find()
            notification?.let { delete(it) }
        }
    }

    /** ✅ 모든 알림 삭제 */
    fun clearAllNotifications() {
        val realm = ApplicationClass.realm
        realm.writeBlocking {
            deleteAll()
        }
    }
}