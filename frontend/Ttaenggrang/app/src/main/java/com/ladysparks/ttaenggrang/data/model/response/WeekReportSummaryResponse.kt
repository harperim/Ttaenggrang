package com.ladysparks.ttaenggrang.data.model.response

data class WeekReportSummaryResponse(
    val fineAmount: Int,
    val incentiveAmount: Int,
    val investmentReturn: Int,
    val reportDate: String,
    val salaryAmount: Int,
    val savingsAmount: Int,
    val studentId: Int,
    val taxAmount: Int,
    val totalExpenses: Int,
    val totalIncome: Int
)