package com.ladysparks.ttaenggrang.data.model.response

data class TaxStudentHistoryResponse(
    val amount: Int,
    val id: Int,
    val overdue: Boolean,
    val paymentDate: String,
    val studentId: Int,
    val taxDescription: String,
    val taxId: Int,
    val taxName: String,
    val taxRate: Double
)