package com.ladysparks.ttaenggrang.data.model.dto
// 교사 주식목록 화면에서만 사용
data class StockSummaryDto(
    val category: String,
    val id: Int,
    val createdDate: String,
    val name: String,
    val priceChangeRate: Int,
    val pricePerShare: Int,
    val transactionFrequency: Int,
    val type: String
)