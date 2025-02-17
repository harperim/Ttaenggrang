package com.ladysparks.ttaenggrang.data.remote

import com.ladysparks.ttaenggrang.data.model.request.TeacherSignUpRequest
import com.ladysparks.ttaenggrang.data.model.response.TeacherSignUpResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SalariesService {
    @POST("salaries/update")
    suspend fun paySalary(@Body teacherName: TeacherSignUpRequest): TeacherSignUpResponse
}