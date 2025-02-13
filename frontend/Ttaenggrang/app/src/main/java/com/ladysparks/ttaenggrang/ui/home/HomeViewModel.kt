package com.ladysparks.ttaenggrang.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.AlarmDto
import com.ladysparks.ttaenggrang.data.model.response.EconomySummaryResponse
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(){

    private val _economySummary= MutableLiveData<EconomySummaryResponse>()
    val economySummary: LiveData<EconomySummaryResponse> get() = _economySummary
    fun fetchEconomySummary(){
        viewModelScope.launch{
            runCatching {
                RetrofitUtil.teacherService.getEconomySummary()
            }.onSuccess {
                _economySummary.value = it.data!!
            }.onFailure {

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
                _studentList.value = emptyList()
            }
        }
    }
}