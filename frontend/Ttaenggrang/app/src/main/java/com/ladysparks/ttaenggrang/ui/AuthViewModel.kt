package com.ladysparks.ttaenggrang.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.request.StudentSignInRequest
import com.ladysparks.ttaenggrang.data.remote.AuthService
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import kotlinx.coroutines.launch

// 로그인 상태 관리를 위한 뷰모델
class AuthViewModel(private val authService: AuthService) : ViewModel() {

    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> get() = _loginStatus

    fun loginAndSaveStudentId(username: String, password: String) {
        viewModelScope.launch {
            try {
                val loginResponse =
                    authService.loginStudent(StudentSignInRequest(username, password))

                if (loginResponse.statusCode == 200) {
                    val studentId = loginResponse.data?.id?.toLong()
                        ?: throw IllegalStateException("학생 ID 없음")

                    // ✅ SharedPreferences에 studentId 저장
                    SharedPreferencesUtil.putUserId(studentId)
                    Log.d("AuthViewModel", "로그인 성공 - studentId 저장됨: $studentId")

                    _loginStatus.postValue(true)  // ✅ 로그인 성공 상태 업데이트
                } else {
                    _loginStatus.postValue(false)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "네트워크 오류 발생: ${e.message}", e)
                _loginStatus.postValue(false)
            }
        }
    }
}