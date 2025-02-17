package com.ladysparks.ttaenggrang.data.model.response

import com.google.gson.annotations.SerializedName

// Sample Code
data class SampleResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("orderId") val orderId: Int,
    @SerializedName("productId") val productId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("name") val productName: String,
    @SerializedName("img") val productImg: String,
    @SerializedName("unitPrice") val unitPrice: Int,
    @SerializedName("sumPrice") val sumPrice: Int,
    @SerializedName("type") val productType: String
)
