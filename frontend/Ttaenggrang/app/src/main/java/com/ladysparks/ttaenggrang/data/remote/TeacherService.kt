package com.ladysparks.ttaenggrang.data.remote

import com.ladysparks.ttaenggrang.data.model.request.StudentMultiCreateRequest
import com.ladysparks.ttaenggrang.data.model.request.StudentSingleCreateRequest
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.data.model.dto.NationInfoDto
import com.ladysparks.ttaenggrang.data.model.response.EconomySummaryResponse
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.model.response.WeekAvgSummaryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface TeacherService {

    // 학생 단일 계정 등록
    @POST("teachers/single-create")
    suspend fun singleCreate(@Body singleStudent: StudentSingleCreateRequest): ApiResponse<Any>

    // 학생 복수 계정 등록 //uploadStudentData
    @Multipart
    @POST("teachers/quick-create")
    suspend fun uploadStudentData(@Query("baseId") baseId: String, @Query("studentCount") studentCount: Int, @Part file: MultipartBody.Part): ApiResponse<List<StudentMultiCreateResponse>>


//    @POST("teachers/quick-create")
//    suspend fun multiCreate(@Body multiStudent: StudentMultiCreateRequest): ApiResponse<Any>


    @GET("teachers/students") // 반학생 전체 조회
    suspend fun getStudentList(): ApiResponse<List<StudentMultiCreateResponse>>

    @GET("teachers/students/{studentId}")
    suspend fun getStudentDetail(@Path("studentId") studentId: String): ApiResponse<Any>

    // Home 정보 조회
    @GET("teachers/{teacherId}/dashboard")
    suspend fun getEconomySummary(): ApiResponse<EconomySummaryResponse>

    @GET("teachers/{teacherId}/dashboard/daily-average")
    suspend fun getWeekAvgSummary(): ApiResponse<List<WeekAvgSummaryResponse>>


    // 국가정보 조회
    @GET("teachers/nations")
    suspend fun getNationInfo(): ApiResponse<NationInfoDto>

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