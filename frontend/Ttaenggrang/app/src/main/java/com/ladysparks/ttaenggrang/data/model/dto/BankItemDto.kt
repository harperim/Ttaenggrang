package com.ladysparks.ttaenggrang.data.model.dto

data class BankItemDto(
    val amount: Int,
    val description: String,
    val durationWeeks: Int,
    val earlyInterestRate: Int, //중도해지 이자율
    val interestRate: Int,
    val name: String,
    val saleEndDate: String,
    val saleStartDate: String
)