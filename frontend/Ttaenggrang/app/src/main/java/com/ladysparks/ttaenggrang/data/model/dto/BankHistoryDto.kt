package com.ladysparks.ttaenggrang.data.model.dto

data class BankHistoryDto(
    val depositHistory: List<DepositHistory>,
    val endDate: String,
    val payoutAmount: Int,
    val savingsName: String,
    val startDate: String
)