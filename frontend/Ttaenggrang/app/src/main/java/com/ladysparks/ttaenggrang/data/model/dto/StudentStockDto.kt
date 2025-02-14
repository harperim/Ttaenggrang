package com.ladysparks.ttaenggrang.data.model.dto

data class StudentStockDto(
    val stockId: Int,          // 보유 주식 ID
    val stockName: String,     // 주식 이름
    val ownedQty: Int,         // 보유 수량
    val purchasePrice: Int,    // 매입 가격 (매입 당시
    val purchaseDate: String,  // 매입 날짜 (ISO 8601 형식: yyyy-MM-dd'T'HH:mm:ss)
    val currentPrice: Int      // 현재 가격
)
