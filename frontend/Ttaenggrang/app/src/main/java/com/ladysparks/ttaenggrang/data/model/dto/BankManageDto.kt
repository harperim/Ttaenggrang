package com.ladysparks.ttaenggrang.data.model.dto

data class BankManageDto(
    val amount: Int,
    val createdAt: String,
    val depositAmount: Int,
    val depositDayOfWeek: String,
    val durationWeeks: Int,
    val endDate: String,
    val id: Int,
    val interestRate: Double,
    val payoutAmount: Int,
    val savingsProductId: Int,
    val startDate: String,
    val status: String,
    val studentId: Int
)