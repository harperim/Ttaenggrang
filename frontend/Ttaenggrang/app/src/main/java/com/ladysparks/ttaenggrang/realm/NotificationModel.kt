package com.ladysparks.ttaenggrang.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID


class NotificationModel : RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString() // ✅ 고유한 ID
    var category: String = "" // REPORT, BANK, NEWS
    var title: String = ""
    var content: String = ""
    var sendTime: String = ""
    var sender: String = "System" // ✅ 발신자 정보
    var receiver: String = "" // TEACHER, STUDENT
    var receivedTime: Long = System.currentTimeMillis()
}
