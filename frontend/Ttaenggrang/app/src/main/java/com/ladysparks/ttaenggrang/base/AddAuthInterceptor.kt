package com.ladysparks.ttaenggrang.base

import com.ladysparks.ttaenggrang.data.model.request.TeacherSignInRequest
import com.ladysparks.ttaenggrang.data.model.request.TeacherSignUpRequest
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.response.TeacherSignInResponse
import com.ladysparks.ttaenggrang.data.model.response.TeacherSignUpResponse
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.IOException

class AddAuthInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()


        //  `Authorization` 헤더 추가하지 않는 경우 : 회원가입, 로그인
        if (request.url.encodedPath.contains("/teachers/signup") ||
            request.url.encodedPath.contains("/teachers/login") ||
            request.url.encodedPath.contains("/students/login")
        ) {
            return chain.proceed(request)
        }

        val builder = request.newBuilder()

        val token = SharedPreferencesUtil.getValue(SharedPreferencesUtil.JWT_TOKEN_KEY, "")
        if (token.isNotEmpty()) {
            builder.addHeader("Authorization", "Bearer $token")  // ✅ JWT 토큰 추가
        }
        return chain.proceed(builder.build())
    }
}