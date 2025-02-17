package com.ladysparks.ttaenggrang.data.remote

import com.google.android.gms.common.api.Api
import com.ladysparks.ttaenggrang.data.model.dto.NationInfoDto
import com.ladysparks.ttaenggrang.data.model.request.StudentSignInRequest
import com.ladysparks.ttaenggrang.data.model.request.TeacherSignInRequest
import com.ladysparks.ttaenggrang.data.model.request.TeacherSignUpRequest
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.response.StudentSignInResponse
import com.ladysparks.ttaenggrang.data.model.response.TeacherSignInResponse
import com.ladysparks.ttaenggrang.data.model.response.TeacherSignUpResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    /*************** 공통 Service ***************/



    /*************** 교사 전용 Service ***************/
    @POST("teachers/signup")
    suspend fun signupTeacher(@Body teacherName: TeacherSignUpRequest): TeacherSignUpResponse

    @POST("teachers/login")
    suspend fun loginTeacher(@Body teacherInfo: TeacherSignInRequest): ApiResponse<TeacherSignInResponse>

    @POST("teachers/logout")
    suspend fun logoutTeacher(): Unit

    @POST("teachers/nations")
    suspend fun nationSetup(@Body nationInfo: NationInfoDto): ApiResponse<NationInfoDto>

    /*************** 학생 전용 Service ***************/
    @POST("students/login")
    suspend fun loginStudent(@Body studentInfo: StudentSignInRequest): ApiResponse<StudentSignInResponse>

    @POST("students/logout")
    suspend fun logoutStudent(): Unit
}