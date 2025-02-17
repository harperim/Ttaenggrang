package com.ladysparks.ttaenggrang.data.model.response

data class WeekAvgSummaryResponse(
    val date: String,
    val averageIncome: Double,
    val averageExpense: Double
)