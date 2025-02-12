package com.ladysparks.ttaenggrang.ui.job

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

class JobViewModel : ViewModel() {

    private val _jobList = MutableLiveData<List<StudentMultiCreateResponse>>()
    val jobList: LiveData<List<StudentMultiCreateResponse>> get() = _jobList

    fun fetchJobList(){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.teacherService.getStudentList()
            }.onSuccess {
                _jobList.value = it.data
            }.onFailure {
                Log.e("JobViewModel", "fetchJobList: Error FeatchJobList ${it}" )
            }
        }
    }

    private val _registerJobResult = MutableLiveData<Boolean>()
    val registerJobResult: LiveData<Boolean> get() = _registerJobResult
    fun registerJob(jobData: JobDto){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.teacherService.registerJob(jobData)
            }.onSuccess {
                _registerJobResult.value = true
                fetchJobList()
            }.onFailure {
                _registerJobResult.value = false
            }
        }
    }

}