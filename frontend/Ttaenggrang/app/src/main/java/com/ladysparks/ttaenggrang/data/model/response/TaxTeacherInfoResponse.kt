package com.ladysparks.ttaenggrang.data.model.response

data class TaxTeacherInfoResponse(
    val id: Int,
    val taxDescription: String,
    val taxName: String,
    val taxRate: Double,
    val teacherId: Int
)
