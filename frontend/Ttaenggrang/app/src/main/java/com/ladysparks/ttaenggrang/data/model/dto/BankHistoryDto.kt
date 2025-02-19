package com.ladysparks.ttaenggrang.data.model.dto

data class BankHistoryDto(
    val depositHistory: List<DepositHistory>,
    val endDate: String, // 만기일
    val payoutAmount: Int, //예상 만기 지급액
    val savingsName: String, // 상품명
    val startDate: String // 시작일
)