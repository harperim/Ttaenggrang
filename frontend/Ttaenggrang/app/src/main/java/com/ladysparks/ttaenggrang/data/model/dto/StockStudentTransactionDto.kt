package com.ladysparks.ttaenggrang.data.model.dto

data class StockStudentTransactionDto(
    val name: String,
    val purchasePricePerShare: Int,
    val shareCount: Int,
    val stockId: Int,
    val studentId: Int,
    val transactionDate: String,
    val transactionType: String,
    val type: String
)