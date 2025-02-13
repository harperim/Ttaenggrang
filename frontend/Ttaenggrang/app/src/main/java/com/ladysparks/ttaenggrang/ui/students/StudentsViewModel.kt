package com.ladysparks.ttaenggrang.ui.students

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.AlarmDto
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.model.response.Teacher
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.util.ApiErrorParser
import kotlinx.coroutines.launch

class StudentsViewModel : ViewModel(){

    // 공통 : Error 처리
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private val _studentList = MutableLiveData<List<StudentMultiCreateResponse>?>() // LiveData를 활용
    val studentList: LiveData<List<StudentMultiCreateResponse>?> get() = _studentList


    // 버튼이 클릭될 때 마다, 학생정보, 재정상태 여부를 판단해서 데이터를 불러오다.
    fun fetchStudentList() {
        viewModelScope.launch {
            runCatching {
                // 서버로부터 알림 데이터 요청
                RetrofitUtil.teacherService.getStudentList()
            }.onSuccess { response ->
                _studentList.value = response.data
            }.onFailure { exception ->
                _studentList.value = null
                Log.e("AlarmViewModel", "Error fetchStudentList ", exception)
            }
        }
    }

    // 재정 관리 탭
   // fun fetch


    // 직업 리스트
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
//                _jobList.value = emptyList()
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
                Log.e("JobViewModel", "fetchJobList 33: Error FeatchJobList ${it.message}" )
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