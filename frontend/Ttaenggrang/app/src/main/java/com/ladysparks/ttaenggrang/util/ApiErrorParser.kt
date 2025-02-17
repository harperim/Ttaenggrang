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
                val (statusCode, message, errorDetails) = parseErrorMessage(errorBody)
                buildErrorMessage(statusCode, message, errorDetails)
            }
            is IOException -> "네트워크 오류 발생: ${error.message}"
            else -> "${error.message}"
        }
    }

    private fun parseErrorMessage(errorBody: ResponseBody?): Triple<Int, String, List<String>> {
        return try {
            val errorJson = JSONObject(errorBody?.string() ?: "{}")
            val statusCode = errorJson.optInt("statusCode", 0)
            val message = errorJson.optString("message", "서버 오류 발생")

            val errorsArray = errorJson.optJSONArray("errors")
            val errorDetails = mutableListOf<String>()

            errorsArray?.let {
                for (i in 0 until it.length()) {
                    val errorObj = it.getJSONObject(i)
                    val field = errorObj.optString("field", "")
                    val errorMessage = errorObj.optString("message", "")
                    if (field.isNotEmpty()) {
                        errorDetails.add("$field: $errorMessage")
                    } else {
                        errorDetails.add(errorMessage)
                    }
                }
            }

            Triple(statusCode, message, errorDetails) // ✅ statusCode, message, errors 반환
        } catch (e: Exception) {
            Triple(0, "서버 응답 파싱 실패", emptyList()) // 기본값 반환
        }
    }

    private fun buildErrorMessage(statusCode: Int, message: String, errorDetails: List<String>): String {
        return if (errorDetails.isNotEmpty()) {
            "[$statusCode] $message\n\n" + errorDetails.joinToString("\n") // 줄바꿈 추가
        } else {
            "[$statusCode] $message"
        }
    }
}