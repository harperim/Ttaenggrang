package com.ladysparks.ttaenggrang.data.model.response

data class TeacherSignUpResponse(
    val data: TeacherDataResponse,
    val message: String,
    val statusCode: Int
)