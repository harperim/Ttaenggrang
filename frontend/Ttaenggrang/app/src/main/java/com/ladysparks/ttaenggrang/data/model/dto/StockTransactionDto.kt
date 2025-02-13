package com.ladysparks.ttaenggrang.data.model.dto

import com.google.gson.annotations.SerializedName

data class StockTransactionDto(
    val id: Long,
    @SerializedName("share_count") val shareCount: Int,
    @SerializedName("trans_date") val transDate: String,
    @SerializedName("purchase_prc") val purchasePrc: Int,
    @SerializedName("total_amt") val totalAmt: Int,
    @SerializedName("return_amt") val returnAmt: Int,
    val transType: TransType,
    @SerializedName("owned_qty") val ownedQty: Int,
    val studentId: Long,
    val stockId: Long
)

enum class TransType {
    @SerializedName("BUY") BUY,
    @SerializedName("SELL") SELL
}
