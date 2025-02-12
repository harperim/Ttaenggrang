package com.ladysparks.ttaenggrang.util

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

object ApiErrorParser {
    fun extractErrorMessage(error: Throwable): String {
        return when (error) {
            is HttpException -> {
                val errorBody = error.response()?.errorBody()
                val (statusCode, message) = parseErrorMessage(errorBody)
                "[$statusCode] $message"
            }
            is IOException -> "네트워크 오류 발생: ${error.message}"
            else -> "알 수 없는 오류: ${error.message}"
        }
    }

    private fun parseErrorMessage(errorBody: ResponseBody?): Pair<Int, String> {
        return try {
            val errorJson = JSONObject(errorBody?.string() ?: "{}")
            val statusCode = errorJson.optInt("statusCode", 0)
            val message = errorJson.optString("message", "서버 오류 발생")
            Pair(statusCode, message) // ✅ statusCode + message 반환
        } catch (e: Exception) {
            Pair(0, "서버 응답 파싱 실패") // 기본값 반환
        }
    }
}