package com.ladysparks.ttaenggrang.data.model.dto

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 그래프 띄울때 필요한 dto
data class StockHistoryDto(
//    val stockId: Int,
//    val stockName: String, // 주식 이름
//    val price: Float,  // 주식 가격
//    val date: String,  // 날짜 (yyyy-MM-dd)
//    val stockId: Int,

    @SerializedName("id") val id: Int,
    @SerializedName("price") val price: Float,
    @SerializedName("priceChangeRate") val priceChangeRate: Float,
    @SerializedName("date") val date: String
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedDate(): String {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
            .format(DateTimeFormatter.ofPattern("MM-dd"))
    }
}

