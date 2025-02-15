package com.ladysparks.ttaenggrang.data.model.dto
// 학생 보유 주식 목록 조회
data class StockStudentDto(
    val stockId: Int,          // 보유 주식 ID
    val stockName: String,     // 주식 이름
    val quantity: Int,         // 현재 보유 수량
    val purchasePrice: Int,    // 구매당시 거래가격
    val purchaseDate: String,  // 매입 날짜 (ISO 8601 형식: yyyy-MM-dd'T'HH:mm:ss)
    val currentPrice: Int      // 현재 가격
)
