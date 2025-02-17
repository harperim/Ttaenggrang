package com.ladysparks.ttaenggrang.data.model.request

data class StockTransactionRequest(
    val stockId: Int,
    val shareCount: Int,
    val studentId: Int
)
