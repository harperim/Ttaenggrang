package com.ladysparks.ttaenggrang.data.model.response

data class StockResponse(
    val currentTotalPrice: Int,
    val priceChangeRate: Int,
    val purchasePrice: Int,
    val quantity: Int,
    val stockName: String
)