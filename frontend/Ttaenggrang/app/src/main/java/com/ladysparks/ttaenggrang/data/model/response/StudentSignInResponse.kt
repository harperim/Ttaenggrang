package com.ladysparks.ttaenggrang.data.model.response

data class StudentSignInResponse(
    val id: Int,
    val username: String,
    val name: String,
    val profileImage: String,
    val token: String
)