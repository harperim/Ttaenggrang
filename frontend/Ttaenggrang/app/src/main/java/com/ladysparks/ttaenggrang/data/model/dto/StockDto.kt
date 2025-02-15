package com.ladysparks.ttaenggrang.data.model.dto

data class StockDto(
    val categoryId: Int,
    val categoryName: String,
    val changeRate: Int,
    val createdAt: String,
    val description: String,
    val id: Int,
    val name: String,
    val priceChangeTime: String,
    val pricePerShare: Int,
    val remainQuantity: Int,
    val teacherId: Int,
    val totalQuantity: Int,
    val type: String,
    val updatedAt: String,
    val weight: Int
)