package com.ladysparks.ttaenggrang.data.remote

import com.ladysparks.ttaenggrang.data.model.request.StudentMultiCreateRequest
import com.ladysparks.ttaenggrang.data.model.request.StudentSingleCreateRequest
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TeacherService {

    @POST("teachers/single-create")
    suspend fun singleCreate(@Body singleStudent: StudentSingleCreateRequest): ApiResponse<Any>

    @POST("teachers/quick-create")
    suspend fun multiCreate(@Body multiStudent: StudentMultiCreateRequest): ApiResponse<Any>

    @GET("teachers/students")
    suspend fun getStudentList(): ApiResponse<List<StudentMultiCreateResponse>>

    @GET("teachers/students/{studentId}")
    suspend fun getStudentDetail(@Path("studentId") studentId: String): ApiResponse<Any>


    /***
     * 직업 정보 관련
     */
    @POST("teachers/jobs/create")
    suspend fun registerJob(@Body jobs: JobDto): ApiResponse<JobDto>

}