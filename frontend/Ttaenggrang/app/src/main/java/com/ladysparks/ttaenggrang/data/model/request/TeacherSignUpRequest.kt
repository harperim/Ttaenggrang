package com.ladysparks.ttaenggrang.data.model.request

data class TeacherSignUpRequest(
    val id: Int,
    val email: String,
    val password1: String,
    val password2: String,
    val name: String,
    val school: String,
    val createdAt: Long
)
