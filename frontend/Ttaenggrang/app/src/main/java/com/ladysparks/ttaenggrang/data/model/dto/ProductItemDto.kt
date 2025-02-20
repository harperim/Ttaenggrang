package com.ladysparks.ttaenggrang.data.model.dto

data class ProductItemDto(
    val amount: Int,
    val createdAt: String,
    val description: String,
    val durationWeeks: Int,
    val earlyInterestRate: Int,
    val id: Int,
    val interestRate: Int,
    val name: String,
    val payoutAmount: Int,
    val saleEndDate: String,
    val saleStartDate: String,
    val subscriberCount: Int,
    val teacherId: Int
)