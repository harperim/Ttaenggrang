package com.ladysparks.ttaenggrang.data.model.dto

data class StockTransactionDto(
    val id: Int,
    val purchasePricePerShare: Int,
    val returnRate: Any,
    val shareQuantity: Int,
    val stockId: Int,
    val studentId: Int,
    val totalPrice: Int,
    val totalQuantity: Int,
    val transactionDate: String,
    val transactionType: String
)