package com.ladysparks.ttaenggrang.data.model.dto

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