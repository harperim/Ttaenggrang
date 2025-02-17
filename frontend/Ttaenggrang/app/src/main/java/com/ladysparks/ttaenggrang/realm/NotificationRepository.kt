package com.ladysparks.ttaenggrang.realm

import com.ladysparks.ttaenggrang.base.ApplicationClass
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults

object NotificationRepository {
    /** ✅ Realm에서 모든 알림 가져오기 */
    fun getAllNotifications(): List<NotificationModel> {
        val realm = ApplicationClass.realm
        return realm.query<NotificationModel>().find() // ✅ 모든 알림 데이터 가져오기
    }

    /** ✅ "TEACHER" 수신자만 필터링하여 알림 가져오기 */
    fun getTeacherNotifications(): List<NotificationModel> {
        val realm = ApplicationClass.realm
        return realm.query<NotificationModel>("receiver == $0", "TEACHER").find()
    }

    /** ✅ "TEACHER" 수신자만 필터링하여 알림 가져오기 */
    fun getStudentNotifications(): List<NotificationModel> {
        val realm = ApplicationClass.realm
        return realm.query<NotificationModel>("receiver == $0", "STUDENT").find()
    }


    /** ✅ 새로운 알림 추가 */
    fun addNotification(notification: NotificationModel) {
        val realm = ApplicationClass.realm
        realm.writeBlocking {
            // ✅ 기존 데이터가 있으면 업데이트, 없으면 추가
            copyToRealm(notification, updatePolicy = io.realm.kotlin.UpdatePolicy.ALL)
        }
    }


    /** ✅ 특정 알림 삭제 */
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