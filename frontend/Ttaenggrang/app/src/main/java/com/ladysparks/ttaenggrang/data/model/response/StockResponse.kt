package com.ladysparks.ttaenggrang.data.model.response

data class StockResponse(
    val stockName: String,
    val quantity: Int,
    val currentTotalPrice: Int,
    val purchasePrice: Int,
    val priceChangeRate: Int,
)