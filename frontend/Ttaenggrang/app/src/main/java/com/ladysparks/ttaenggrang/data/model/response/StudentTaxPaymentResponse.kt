package com.ladysparks.ttaenggrang.data.model.response

data class StudentTaxPaymentResponse(
    val taxId: Int,
    val amount: Int,
    val status: String // 이거 다시 물어보기
)

