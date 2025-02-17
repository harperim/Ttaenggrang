package com.ladysparks.ttaenggrang.data.model.dto
// 그래프 띄울때 필요한 dto
data class StockHistoryDto(
    val stockId: Int,
    val stockName: String, // 주식 이름
    val price: Float,  // 주식 가격
    val date: String,  // 날짜 (yyyy-MM-dd)
)