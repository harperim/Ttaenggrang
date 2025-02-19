package com.ladysparks.ttaenggrang.data.model.dto

data class DepositHistory(
    val amount: Int,
    val balance: Int,
    val id: Int,
    val interestRate: Double,
    val transactionDate: String,
    val transactionType: String
)