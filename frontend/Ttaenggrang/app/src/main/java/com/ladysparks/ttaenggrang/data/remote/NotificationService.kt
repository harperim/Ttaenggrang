package com.ladysparks.ttaenggrang.data.remote

import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import retrofit2.http.POST
import retrofit2.http.Query


interface NotificationService {
    @POST("notifications/token")
    suspend fun updateFCMToken(@Query("token") token: String): ApiResponse<String>
//    suspend fun updateFCMToken(@Query("token") token: String): Response<ApiResponse<String>>
}