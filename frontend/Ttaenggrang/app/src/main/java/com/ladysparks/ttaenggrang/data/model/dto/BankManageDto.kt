package com.ladysparks.ttaenggrang.data.model.dto

data class BankManageDto(
    val amount: Int, //매주 납입 금액
    val createdAt: String,
    val depositAmount: Int, //현재까지 납입 총 금액
    val depositDayOfWeek: String,
    val durationWeeks: Int,
    val endDate: String,
    val id: Int,
    val interestRate: Double,
    val payoutAmount: Int, //예상지급액
    val savingsProductId: Int, // 상품명
    val startDate: String,
    val status: String,
    val studentId: Int
)