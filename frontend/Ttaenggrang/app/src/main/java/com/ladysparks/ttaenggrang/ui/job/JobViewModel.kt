package com.ladysparks.ttaenggrang.ui.job

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.util.ApiErrorParser
import kotlinx.coroutines.launch

class JobViewModel : ViewModel() {

    // 공통 : Error 처리
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // 직업 리스트 조회
    private val _jobList = MutableLiveData<List<JobDto>>()
    val jobList: LiveData<List<JobDto>> get() = _jobList
    fun fetchJobList(){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.teacherService.getJobList()
            }.onSuccess {
                _jobList.value = it.data ?: emptyList()
                Log.e("JobViewModel", "fetchJobList 22: Error FeatchJobList ${it.message}" )
            }.onFailure {
                _jobList.value = emptyList()
                Log.e("JobViewModel", "fetchJobList 33: Error FeatchJobList ${it.message}" )
            }
        }
    }

    // 새 직업 등록 결과
    private val _registerJobResult = MutableLiveData<JobDto?>()
    val registerJobResult: LiveData<JobDto?> get() = _registerJobResult
    fun registerJob(jobData: JobDto){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.teacherService.registerJob(jobData)
            }.onSuccess {
                _registerJobResult.value = it.data
                fetchJobList()
            }.onFailure {
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }

}