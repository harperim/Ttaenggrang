package com.ladysparks.ttaenggrang.data.remote

import com.ladysparks.ttaenggrang.data.model.request.StudentMultiCreateRequest
import com.ladysparks.ttaenggrang.data.model.request.StudentSingleCreateRequest
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.data.model.dto.NationInfoDto
import com.ladysparks.ttaenggrang.data.model.response.BankTransactionsResponse
import com.ladysparks.ttaenggrang.data.model.response.EconomySummaryResponse
import com.ladysparks.ttaenggrang.data.model.response.MainStudentSummary
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.model.response.WeekAvgSummaryResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StudentService {


    @GET("students/{studentId}/dashboard")
    suspend fun getStudentDataSummary(): ApiResponse<MainStudentSummary>

    @GET("students/{studentId}/dashboard/bank-transactions")
    suspend fun getBankTransactions(): ApiResponse<List<BankTransactionsResponse>>




    @POST("teachers/jobs/create")
    suspend fun registerJob(@Body jobs: JobDto): ApiResponse<JobDto>


    // ----------- 주급, 인센티브
    @POST("salaries/distribute-base")
    suspend fun payStudentWeeklySalary(): ApiResponse<String>

    @POST("salaries/incentives")
    suspend fun payStudentBonus(@Body request: Map<String, Int>): ApiResponse<String>


}