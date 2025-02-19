package com.ladysparks.ttaenggrang.data.model.dto

data class TaxDto(
    val id: Int? = null,
    val teacherId: Int? = null,
    val taxName: String,
    val taxRate: Double,
    val taxDescription: String
)