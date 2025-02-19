package com.ladysparks.ttaenggrang.data.model.dto

data class DepositHistory(
    val amount: Int, // 기본 납입금액
    val balance: Int, //잔액
    val id: Int,
    val interestRate: Double,
    val transactionDate: String,
    val transactionType: String
)