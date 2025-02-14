package com.ladysparks.ttaenggrang.data.model.dto

import com.google.gson.annotations.SerializedName

data class StockStudentTransactionDto(
    @SerializedName("studentId") val studentId: Int,         // 학생 ID
    @SerializedName("stockId") val stockId: Int,             // 주식 ID
    @SerializedName("transType") val transType: TransType,    // 거래 유형 (BUY, SELL)
    @SerializedName("share_count") val shareCount: Int,       // 거래 수량
    @SerializedName("trans_date") val transDate: String,      // 거래 날짜 (ISO 8601: yyyy-MM-dd'T'HH:mm:ss)
    @SerializedName("purchase_prc") val purchasePrice: Int,   // 매수/매도 가격
    @SerializedName("name") val stockName: String,            // 주식명
    @SerializedName("type") val stockType: String             // 주식 유형 (예: 보통주, 우선주 등)
)

