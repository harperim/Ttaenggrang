package com.ladysparks.ttaenggrang.data.model.response

import com.google.gson.annotations.SerializedName
import com.ladysparks.ttaenggrang.data.model.dto.BankTransactionDto

data class BankTransactionResponse(
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: BankTransactionDto
)
