package com.ladysparks.ttaenggrang.data.model.response

data class StoreStudenItemListResponse(
    val approved: Boolean,
    val createdAt: String,
    val description: String,
    val id: Int,
    val image: String,
    val name: String,
    val price: Int,
    val quantity: Int,
    val sellerId: Int,
    val sellerName: String,
    val sellerType: String,
    val updatedAt: String
)
