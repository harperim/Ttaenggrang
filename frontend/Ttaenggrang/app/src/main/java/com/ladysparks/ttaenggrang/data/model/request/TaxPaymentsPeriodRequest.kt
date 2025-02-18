package com.ladysparks.ttaenggrang.data.model.request

import java.sql.Date

class TaxPaymentsPeriodRequest (
    val studentId: Int,
    val startDate: Date,
    val endDate: Date
)