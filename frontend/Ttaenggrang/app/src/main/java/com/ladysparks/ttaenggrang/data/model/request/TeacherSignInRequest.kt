package com.ladysparks.ttaenggrang.data.model.request

data class TeacherSignInRequest(
    val email: String? = null,
    val password: String? = null,
    val fcmToken: String,
)