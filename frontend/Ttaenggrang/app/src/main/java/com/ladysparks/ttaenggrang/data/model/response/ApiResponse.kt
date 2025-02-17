package com.ladysparks.ttaenggrang.data.model.response

data class ApiResponse<T>(
    val statusCode: Int,
    val message: String,
    val data: T? = null  // API마다 다른 데이터를 포함하기 위해 Generic 사용
)