package com.ladysparks.ttaenggrang.data.remote

import android.app.job.JobInfo
import com.ladysparks.ttaenggrang.data.model.request.StudentMultiCreateRequest
import com.ladysparks.ttaenggrang.data.model.request.StudentSingleCreateRequest
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.data.model.dto.NationInfoDto
import com.ladysparks.ttaenggrang.data.model.dto.TaxDto
import com.ladysparks.ttaenggrang.data.model.response.BankTransactionsResponse
import com.ladysparks.ttaenggrang.data.model.response.EconomySummaryResponse
import com.ladysparks.ttaenggrang.data.model.response.MainStudentSummary
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.model.response.WeekAvgSummaryResponse
import com.ladysparks.ttaenggrang.data.model.response.WeekReportStudentGrowth
import com.ladysparks.ttaenggrang.data.model.response.WeekReportSummaryResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StudentService {


    @GET("students/{studentId}/dashboard")
    suspend fun getStudentDataSummary(): ApiResponse<MainStudentSummary>

    @GET("students/{studentId}/dashboard/bank-transactions")
    suspend fun getBankTransactions(): ApiResponse<List<BankTransactionsResponse>>

//getStudentJobInfo

    @GET("students/job")
    suspend fun getStudentJobInfo(): ApiResponse<JobDto>

    @GET("taxes/")   // 세금 리스트항목 조회
    suspend fun getTaxesList(): ApiResponse<List<TaxDto>>

    @POST("teachers/jobs/create")
    suspend fun registerJob(@Body jobs: JobDto): ApiResponse<JobDto>


    // ----------- 주급, 인센티브
    @POST("salaries/distribute-base")
    suspend fun payStudentWeeklySalary(): ApiResponse<String>

    @POST("salaries/incentives")
    suspend fun payStudentBonus(@Body request: Map<String, Int>): ApiResponse<String>

    // ----------------- 주간 통계 보고서
    // weeklyReport
    @GET("weekly-report") // 이번주 금융 활동 요약
    suspend fun getWeeklyReport(): ApiResponse<WeekReportSummaryResponse>

    @GET("weekly-report/growth") // 이번주 금융 성적표
    suspend fun getWeeklyGrowth(): ApiResponse<WeekReportStudentGrowth>

    @GET("weekly-report/latest-ai-feedback") // 최신 AI 피드백 조회
    suspend fun getWeeklyAiFeedback(): ApiResponse<String>


}