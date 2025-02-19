package com.ladysparks.ttaenggrang.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.data.model.dto.TaxDto
import com.ladysparks.ttaenggrang.data.model.response.BankTransactionsResponse
import com.ladysparks.ttaenggrang.data.model.response.EconomySummaryResponse
import com.ladysparks.ttaenggrang.data.model.response.MainStudentSummary
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.model.response.WeekAvgSummaryResponse
import com.ladysparks.ttaenggrang.data.model.response.WeekReportStudentGrowth
import com.ladysparks.ttaenggrang.data.model.response.WeekReportSummaryResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.util.ApiErrorParser
import kotlinx.coroutines.launch

class HomeStudentViewModel : ViewModel(){

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private val _studentSummaryData= MutableLiveData<MainStudentSummary?>()
    val studentSummaryData: LiveData<MainStudentSummary?> get() = _studentSummaryData
    fun fetchStudentSummary(){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.studentService.getStudentDataSummary()
            }.onSuccess {
                _studentSummaryData.value = it.data
            }.onFailure {
                //                _studentList.value = emptyList()
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }

    private val _bankTransactionsList= MutableLiveData<List<BankTransactionsResponse?>>()
    val bankTransactionsList: LiveData<List<BankTransactionsResponse?>> get() = _bankTransactionsList
    fun fetchBankTransactions(){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.studentService.getBankTransactions()
            }.onSuccess {
                Log.d("TAG", "fetchBankTransactions: ${it.data}")
                _bankTransactionsList.value = it.data ?: emptyList()
            }.onFailure {
                Log.d("TAG", "fetchBankTransactions ERROR: ${it}")
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)

            }
        }
    }

    // 학생 직업 + 월급 조회
    private val _studentJobInfo= MutableLiveData<JobDto?>()
    val studentJobInfo: LiveData<JobDto?> get() = _studentJobInfo
    fun fetchJobInfo(){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.studentService.getStudentJobInfo()
            }.onSuccess {
                _studentJobInfo.value = it.data
            }.onFailure {
                //                _studentList.value = emptyList()
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }

    // 세금 리스트 조회
    private val _taxesList= MutableLiveData<List<TaxDto>>()
    val taxesList: LiveData<List<TaxDto>> get() = _taxesList
    fun fetchTaxesList(){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.studentService.getTaxesList()
            }.onSuccess {
                _taxesList.value = it.data ?: emptyList()
            }.onFailure {
                //                _studentList.value = emptyList()
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }


    // 주간 리포트 1 : 이번주 내 금융 성적표
    private val _weeklyGrowth = MutableLiveData<WeekReportStudentGrowth?>()
    val weeklyGrowth: LiveData<WeekReportStudentGrowth?> get() = _weeklyGrowth
    fun fetchWeeklyGrowth(){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.studentService.getWeeklyGrowth()
            }.onSuccess {
                _weeklyGrowth.value = it.data
            }.onFailure {
                //                _studentList.value = emptyList()
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }

    // 주간 리포트 2 : 최신 AI 피드백
    private val _weekAiFeedback = MutableLiveData<String>()
    val weekAiFeedback: LiveData<String> get() = _weekAiFeedback
    fun fetchWeeklyAiFeedback(){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.studentService.getWeeklyAiFeedback()
            }.onSuccess {
                _weekAiFeedback.value = it.data ?: ""
            }.onFailure {
                //                _studentList.value = emptyList()
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }

    // 주간 리포트 3 : 이번주 금융 활동 요약
    private val _weekReportSummary = MutableLiveData<WeekReportSummaryResponse?>()
    val weekReportSummary: LiveData<WeekReportSummaryResponse?> get() = _weekReportSummary
    fun fetchWeeklyReportSummary(){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.studentService.getWeeklyReport()
            }.onSuccess {
                _weekReportSummary.value = it.data
            }.onFailure {
                //                _studentList.value = emptyList()
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }
}