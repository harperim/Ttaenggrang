package com.ladysparks.ttaenggrang.data.model.dto

data class StockTransactionHistoryDto(
    val name: String, //주식이름
    val purchasePricePerShare: Int, //1주당 가격
    val shareCount: Int, //구매 주식 수량
    val stockId: Int, //주식 id
    val studentId: Int, //학생 id
    val transactionDate: String, // 거래 날짜
    val transactionType: String, //거래 타입 buy/sell
    val type: String, // 주식타입 일반주식/etf
    val currentPrice: Int
)