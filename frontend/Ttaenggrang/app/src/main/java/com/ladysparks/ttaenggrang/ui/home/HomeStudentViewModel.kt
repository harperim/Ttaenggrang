package com.ladysparks.ttaenggrang.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped
import com.ladysparks.ttaenggrang.data.model.response.BankTransactionsResponse
import com.ladysparks.ttaenggrang.data.model.response.EconomySummaryResponse
import com.ladysparks.ttaenggrang.data.model.response.MainStudentSummary
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.model.response.WeekAvgSummaryResponse
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
//                RetrofitUtil.studentService.getStudentDataSummary()
                RetrofitUtil.studentService.getBankTransactions()

            }.onSuccess {
                Log.d("TAG", "fetchBankTransactions: ${it.data}")
                _bankTransactionsList.value = it.data ?: emptyList()
            }.onFailure {
                //                _studentList.value = emptyList()
                Log.d("TAG", "fetchBankTransactions ERROR: ${it}")
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)

            }
        }
    }


}