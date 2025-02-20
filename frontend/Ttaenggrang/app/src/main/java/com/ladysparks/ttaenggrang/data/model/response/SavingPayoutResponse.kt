package com.ladysparks.ttaenggrang.data.model.response

data class SavingPayoutResponse(
    val createdAt: String,
    val id: Int,
    val interestAmount: Int,
    val isPaid: Boolean,
    val payoutAmount: Int,
    val payoutDate: String,
    val payoutType: String,
    val principalAmount: Int,
    val savingsSubscriptionId: Int
)