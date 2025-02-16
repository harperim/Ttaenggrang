package com.ladysparks.ttaenggrang.data.model.response

data class StudentSavingResponse(
    val amount: Int,
    val interest: Double,
    val savingsName: String,
    val subscriptionDate: String,
    val totalAmount: Double
)