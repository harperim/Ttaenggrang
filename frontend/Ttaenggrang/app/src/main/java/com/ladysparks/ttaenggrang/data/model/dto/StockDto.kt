package com.ladysparks.ttaenggrang.data.model.dto

import com.google.gson.annotations.SerializedName


data class StockDto(
    val id: Long,
    val name: String,
    @SerializedName("price_per") val pricePer: Int,
    @SerializedName("total_qty") val totalQty: Int,
    @SerializedName("remain_qty") val remainQty: Int,
    val description: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("changeRate") val changeRate: Int,
    @SerializedName("openTime") val openTime: String,  // ✅ 객체 → 문자열로 변경
    @SerializedName("closeTime") val closeTime: String,  // ✅ 객체 → 문자열로 변경
    @SerializedName("isMarketActive") val isMarketActive: Boolean,
    @SerializedName("priceChangeTime") val priceChangeTime: String,
    val weight: Double,
    @SerializedName("teacher_id") val teacherId: Long,
    val categoryId: Long,
    val categoryName: String
)


//data class LocalTimeDto(
//    val hour: Int,
//    val minute: Int,
//    val second: Int,
//    val nano: Int
//)