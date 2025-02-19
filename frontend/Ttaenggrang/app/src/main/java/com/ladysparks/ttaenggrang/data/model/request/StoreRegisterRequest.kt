package com.ladysparks.ttaenggrang.data.model.request

data class StoreRegisterRequest(
    val name: String,
    val description: String = "",  // 기본값을 빈 문자열로 설정
    val image: String = "",        // 기본값을 빈 문자열로 설정
    val price: Int,
    val quantity: Int
)