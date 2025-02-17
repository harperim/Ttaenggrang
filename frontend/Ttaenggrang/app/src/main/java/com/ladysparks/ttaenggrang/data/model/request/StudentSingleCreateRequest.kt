package com.ladysparks.ttaenggrang.data.model.request

data class StudentSingleCreateRequest(
    val username: String, // 학생 ID
    val name: String? = null, // 실제 이름(한글)
    val password: String,
    val profileImage: String
)