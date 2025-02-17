package com.ladysparks.ttaenggrang.data.model.response

data class BankAccountResponse(
    val accountNumber: String,
    val balance: Int,
    val createdAt: String,
    val id: Int
)