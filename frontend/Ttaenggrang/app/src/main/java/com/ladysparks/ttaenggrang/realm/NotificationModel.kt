package com.ladysparks.ttaenggrang.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.UUID

class NotificationModel : RealmObject {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString() // ✅ 고유한 ID
    var category: String = "" // Report, OTHER....
    var title: String = ""
    var content: String = ""
    var time: Long = 0 // ✅ 수신 시간   ////
    var sender: String = "System" // ✅ 발신자 정보
    var receiver: String = "" // TEACHER, STUDENT
}
