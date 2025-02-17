package com.ladysparks.ttaenggrang.data.remote

import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.response.StoreStudenItemListResponse
import com.ladysparks.ttaenggrang.data.model.response.StoreStudentPurchaseHistory
import retrofit2.http.GET

interface StoreService {
    @GET("item-products")
    suspend fun getStudentItemList(): ApiResponse<List<StoreStudenItemListResponse>>

    @GET("item-transactions/purchase")
    suspend fun getStudentPurchaseHistory(): ApiResponse<List<StoreStudentPurchaseHistory>>

    @GET("item-products/teacher")
    suspend fun getTeacherItemList(): ApiResponse<List<StoreStudenItemListResponse>>
}