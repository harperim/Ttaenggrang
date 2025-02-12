package com.ladysparks.ttaenggrang.data.remote

import com.ladysparks.ttaenggrang.data.model.dto.TaxDto
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.response.StudentTaxPaymentResponse
import com.ladysparks.ttaenggrang.data.model.response.TeacherTaxInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface NotificationService {
    @POST("notifications/token")
    suspend fun updateFCMToken(@Query("token") token: String): ApiResponse<String>
//    suspend fun updateFCMToken(@Query("token") token: String): Response<ApiResponse<String>>
}