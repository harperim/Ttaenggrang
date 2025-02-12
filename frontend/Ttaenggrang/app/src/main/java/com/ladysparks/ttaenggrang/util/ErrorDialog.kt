package com.ladysparks.ttaenggrang.util

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import org.json.JSONObject

// Activity 확장 함수
fun Activity.showErrorDialog(error: Throwable) {
    if (this.isFinishing) return // UI가 닫혀있으면 다이얼로그를 띄우지 않음
    showDialog(this, error)
}

// Fragment 확장 함수
fun Fragment.showErrorDialog(error: Throwable) {
    if (!isAdded) return // 프래그먼트가 분리된 경우 방지
    showDialog(requireContext(), error)
}

// 다이얼로그 표시 함수 (공통)
private fun showDialog(context: Context, error: Throwable) {
    val errorMessage = ApiErrorParser.extractErrorMessage(error) // ✅ 에러 메시지 변환

    AlertDialog.Builder(context)
        .setTitle("Error")
        .setMessage(errorMessage)
        .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
        .show()
}

// JSON 오류 메시지 파서
object ErrorDialog {
    fun extractErrorMessage(error: Throwable): String {
        return try {
            val json = JSONObject(error.message ?: "{}") // JSON 파싱
            val message = json.optString("message", "알 수 없는 오류가 발생했습니다.")

            val errorsArray = json.optJSONArray("errors") // errors 배열 체크
            val errorMessages = mutableListOf<String>()

            errorsArray?.let {
                for (i in 0 until it.length()) {
                    val errorObj = it.getJSONObject(i)
                    val field = errorObj.optString("field", "")
                    val errorMessage = errorObj.optString("message", "")
                    if (field.isNotEmpty()) {
                        errorMessages.add("$field: $errorMessage")
                    } else {
                        errorMessages.add(errorMessage)
                    }
                }
            }

            if (errorMessages.isNotEmpty()) {
                "$message\n\n" + errorMessages.joinToString("\n") // 줄바꿈 추가
            } else {
                message
            }
        } catch (e: Exception) {
            "오류 메시지를 불러올 수 없습니다."
        }
    }
}