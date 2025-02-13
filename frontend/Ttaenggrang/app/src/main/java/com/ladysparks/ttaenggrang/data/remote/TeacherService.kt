package com.ladysparks.ttaenggrang.data.remote

import com.ladysparks.ttaenggrang.data.model.request.StudentMultiCreateRequest
import com.ladysparks.ttaenggrang.data.model.request.StudentSingleCreateRequest
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.data.model.response.EconomySummaryResponse
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.model.response.WeekAvgSummaryResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TeacherService {

    // init
    @POST("teachers/single-create")
    suspend fun singleCreate(@Body singleStudent: StudentSingleCreateRequest): ApiResponse<Any>

    @POST("teachers/quick-create")
    suspend fun multiCreate(@Body multiStudent: StudentMultiCreateRequest): ApiResponse<Any>

    @GET("teachers/students") // 반학생 전체 조회
    suspend fun getStudentList(): ApiResponse<List<StudentMultiCreateResponse>>

    @GET("teachers/students/{studentId}")
    suspend fun getStudentDetail(@Path("studentId") studentId: String): ApiResponse<Any>

    // Home 정보 조회
    @GET("teachers/{teacherId}/dashboard")
    suspend fun getEconomySummary(): ApiResponse<EconomySummaryResponse>

    @GET("teachers/{teacherId}/dashboard/daily-average-income-expense")
    suspend fun getWeekAvgSummary(): ApiResponse<List<WeekAvgSummaryResponse>>


    /***
     * 직업 정보 관련
     */
    // ----------- 직업 관련
    @GET("teachers/jobs/class")
    suspend fun getJobList(): ApiResponse<List<JobDto>>

    @POST("teachers/jobs/create")
    suspend fun registerJob(@Body jobs: JobDto): ApiResponse<JobDto>


    // ----------- 주급, 인센티브
    @POST("salaries/distribute-base")
    suspend fun payStudentWeeklySalary(): ApiResponse<String>

    @POST("salaries/incentives")
    suspend fun payStudentBonus(@Body request: Map<String, Int>): ApiResponse<String>


}