package com.ladysparks.ttaenggrang.data.model.response

data class WeekReportStudentGrowth(
    val studentId: Int,
    val classAverageSummary: ClassAverageSummary,
    val lastWeekSummary: LastWeekSummary,
    val thisWeekSummary: ThisWeekSummary
)