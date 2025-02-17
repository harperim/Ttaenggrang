package com.ladysparks.ttaenggrang.data.model.response

data class BankTransactionsResponse (
    val accountBalance: Int,
    val amount: Int,
    val transactionDate: String,
    val transactionId: Int,
    val transactionType: String
)