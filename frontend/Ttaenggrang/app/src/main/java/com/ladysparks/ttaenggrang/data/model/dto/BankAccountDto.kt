package com.ladysparks.ttaenggrang.data.model.dto

data class BankAccountDto(
    val accountNumber: String, // 계좌번호
    val balance: Int           // 거래 가능 현금 (보유 금액)
)
