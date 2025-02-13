package com.ladysparks.ttaenggrang.data.model.dto

import com.google.gson.annotations.SerializedName

data class StockTransactionDto(
    val id: Int,
    @SerializedName("share_count") val shareCount: Int,
    @SerializedName("trans_date") val transDate: String,
    @SerializedName("purchase_prc") val purchasePrc: Int,
    @SerializedName("total_amt") val totalAmt: Int, // 총 거래 금액
    //@SerializedName("return_amt") val returnAmt: Int,
    val transType: TransType,
    @SerializedName("owned_qty") val ownedQty: Int,
    val studentId: Int,
    val stockId: Int
)

enum class TransType {
    @SerializedName("BUY") BUY,
    @SerializedName("SELL") SELL
}
