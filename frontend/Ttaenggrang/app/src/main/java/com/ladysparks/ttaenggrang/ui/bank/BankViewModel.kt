package com.example.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.BankHistoryDto
import com.ladysparks.ttaenggrang.data.model.dto.BankManageDto
import com.ladysparks.ttaenggrang.data.model.response.BankAccountCountResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

class BankViewModel : ViewModel() {

    // 적금 가입 내역을 담을 LiveData
    private val _savingsList = MutableLiveData<List<BankManageDto>>()
    val savingsList: LiveData<List<BankManageDto>> get() = _savingsList

    // 에러 메시지 관리
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // 학생 적금 개수 LiveData
    private val _bankAccountCount = MutableLiveData<BankAccountCountResponse?>()
    val bankAccountCount: LiveData<BankAccountCountResponse?> get() = _bankAccountCount

    // 적금 내역 LiveData
    private val _bankHistory = MutableLiveData<BankHistoryDto?>()
    val bankHistory: LiveData<BankHistoryDto?> get() = _bankHistory

  // 가입 적금 전체 조회
    fun fetchUserSavings() {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.bankService.getUserSavings()
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        _savingsList.value = apiResponse.data ?: emptyList() // ✅ 리스트 그대로 할당
                        Log.d("BankViewModel", "적금 가입 내역 불러오기 성공: ${_savingsList.value}")
                    } ?: run {
                        _errorMessage.value = "데이터를 불러올 수 없습니다."
                        Log.e("BankViewModel", "적금 가입 내역이 null입니다.")
                    }
                } else {
                    _errorMessage.value = "서버 응답 오류: ${response.code()}"
                    Log.e("BankViewModel", "서버 응답 오류: ${response.code()}")
                }
            }.onFailure { e ->
                _errorMessage.value = "네트워크 오류: ${e.message}"
                Log.e("BankViewModel", "네트워크 오류: ${e.message}")
            }
        }
    }

    fun fetchUserSavingCount() {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.bankService.getUserSavingCount() // 네트워크 요청 실행
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        apiResponse.data?.let { data ->
                            _bankAccountCount.postValue(data) // ✅ postValue 사용
                            Log.d("BankViewModel", "적금 개수: ${data.savingsProductCount}, 예금 개수: ${data.depositProductCount}")
                        } ?: run {
                            _errorMessage.postValue("데이터를 불러올 수 없습니다.")
                            Log.e("BankViewModel", "예금/적금 개수가 null입니다.")
                        }
                    }
                } else {
                    _errorMessage.postValue("서버 응답 오류: ${response.code()}")
                    Log.e("BankViewModel", "서버 응답 오류: ${response.code()}")
                }
            }.onFailure { e ->
                _errorMessage.postValue("네트워크 오류: ${e.message}")
                Log.e("BankViewModel", "네트워크 오류: ${e.message}")
            }
        }
    }

    // 특정 적금 내역
    fun fetchBankHistory(savingsSubscriptionId: Int) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.bankService.getBankHistory(savingsSubscriptionId) // 네트워크 요청 실행
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        _bankHistory.postValue(apiResponse.data) // ✅ postValue 사용하여 업데이트
                        Log.d("BankHistoryViewModel", "적금 내역 불러오기 성공: $apiResponse")
                    } ?: run {
                        _errorMessage.postValue("데이터를 불러올 수 없습니다.")
                        Log.e("BankHistoryViewModel", "적금 내역이 null입니다.")
                    }
                } else {
                    _errorMessage.postValue("서버 응답 오류: ${response.code()}")
                    Log.e("BankHistoryViewModel", "서버 응답 오류: ${response.code()}")
                }
            }.onFailure { e ->
                _errorMessage.postValue("네트워크 오류: ${e.message}")
                Log.e("BankHistoryViewModel", "네트워크 오류: ${e.message}")
            }
        }
    }




}
