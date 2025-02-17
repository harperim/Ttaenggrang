package com.ladysparks.ttaenggrang.data.model.response

data class TeacherDataResponse(
    val createdAt: String,
    val email: String,
    val id: Int,
    val name: String,
    val password1: Any,
    val password2: Any,
    val school: String
)