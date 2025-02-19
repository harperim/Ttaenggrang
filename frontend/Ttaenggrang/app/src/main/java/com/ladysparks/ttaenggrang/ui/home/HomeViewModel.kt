package com.ladysparks.ttaenggrang.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped
import com.ladysparks.ttaenggrang.data.model.response.EconomySummaryResponse
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.model.response.WeekAvgSummaryResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.util.ApiErrorParser
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(){

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private val _economySummary= MutableLiveData<EconomySummaryResponse>()
    val economySummary: LiveData<EconomySummaryResponse> get() = _economySummary
    fun fetchEconomySummary(){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.teacherService.getEconomySummary()
            }.onSuccess {
                _economySummary.value = it.data!!
            }.onFailure {
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }

    private val _studentList= MutableLiveData<List<StudentMultiCreateResponse>>()
    val studentList: LiveData<List<StudentMultiCreateResponse>> get() = _studentList
    fun fetchStudentList(){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.teacherService.getStudentList()
            }.onSuccess {
                _studentList.value = it.data ?: emptyList()
            }.onFailure {
                Log.e("Error", "fetchStudentList: ${it.message}")
                //_errorMessage.value = ApiErrorParser.extractErrorMessage(it)

            }
        }
    }

    // _economySummary
    private val _weekAvgSummary= MutableLiveData<List<WeekAvgSummaryResponse>>()
    val weekAvgSummary: LiveData<List<WeekAvgSummaryResponse>> get() = _weekAvgSummary
    fun fetchWeekAvgSummary(){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.teacherService.getWeekAvgSummary()
            }.onSuccess {
                _weekAvgSummary.value = it.data ?: emptyList()
            }.onFailure {
                Log.e("Error", "fetchStudentList: ${it.message}")
//                _weekAvgSummary.value = emptyList()
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }
    // 주급 지급
    private val _weeklyPaymentStatus = MutableLiveData<Boolean>()
    val weeklyPaymentStatus: LiveData<Boolean> get() = _weeklyPaymentStatus
    fun processStudentWeeklySalary(){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.teacherService.payStudentWeeklySalary()
            }.onSuccess {
                _weeklyPaymentStatus.value = true
            }.onFailure {
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
                Log.e("TAG", "processStudentWeeklySalary: ${it.message}")
            }
        }
    }

    // 인센티브 지급
    private val _bonusPaymentStatus = MutableLiveData<Boolean>()
    val bonusPaymentStatus: LiveData<Boolean> get() = _bonusPaymentStatus
    fun processStudentBonus(studentId: Int, incentive: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.teacherService.payStudentBonus(mapOf("studentId" to studentId, "incentive" to incentive))
            }.onSuccess {
                _bonusPaymentStatus.value = true
            }.onFailure {
                Log.e("TAG", "processStudentBonus 에러다: ${it.message}")
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }

}