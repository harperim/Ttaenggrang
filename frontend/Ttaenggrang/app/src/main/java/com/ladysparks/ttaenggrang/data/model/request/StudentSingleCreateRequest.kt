package com.ladysparks.ttaenggrang.data.model.request

data class StudentSingleCreateRequest(
    val name: String? = null, // 실제 이름(한글)
    val jobId: Int,
    val username: String, // 학생 ID
    val password: String
)