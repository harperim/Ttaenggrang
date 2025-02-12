package com.ladysparks.ttaenggrang.data.model.response

import com.google.gson.annotations.SerializedName

data class TeacherSignInResponse(
    val id: Int?,
    val email: String,
    val name: String,
    val school: String,
    val token: String,
    val tempTF: Boolean = true // ✅ 기본값 유지하도록 설정
)