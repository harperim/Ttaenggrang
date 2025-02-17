package com.ladysparks.ttaenggrang.data.model.response

// 학생 조회시 사용
data class Teacher(
    val createdAt: String,
    val email: String,
    val id: Int,
    val name: String,
    val password: String,
    val school: String,
    val etfs: List<Any>
)