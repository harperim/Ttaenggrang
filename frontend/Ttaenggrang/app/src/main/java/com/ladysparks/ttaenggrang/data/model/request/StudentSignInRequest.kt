package com.ladysparks.ttaenggrang.data.model.request

data class StudentSignInRequest(
    val username: String,
    val password: String,
    val fcmToken: String,
)