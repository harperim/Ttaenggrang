package com.ladysparks.ttaenggrang.ui.nation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.AlarmDto
import com.ladysparks.ttaenggrang.data.model.response.EconomySummaryResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

class NationViewModel : ViewModel(){
//    private val _registerJobResult = MutableLiveData<Boolean>()
//    val registerJobResult: LiveData<Boolean> get() = _registerJobResult
//    fun registerJob(jobData: JobDto){
//        viewModelScope.launch {
//            runCatching {
//                RetrofitUtil.teacherService.registerJob(jobData)
//            }.onSuccess {
//                _registerJobResult.value = true
//                fetchJobList()
//            }.onFailure {
//                _registerJobResult.value = false
//            }
//        }
//    }

    private val _nationInfoList = MutableLiveData<EconomySummaryResponse>()
    val nationInfoData: LiveData<EconomySummaryResponse> get() = _nationInfoList
    fun fetchNationData(){
        // 만약 국가 정복 없으면 등록으로 안내 해야함.
        viewModelScope.launch {
            runCatching {
               // RetrofitUtil.teacherService.getNationInfo()
            }.onSuccess {
//                _nationInfoList.value = _nationInfoList.value
            }.onFailure {
//                val errorMessage = ApiErrorParser.extractErrorMessage(it)
//                if (errorMessage.contains("등록된 국가가 없습니다.")) {
//                    _nationInfoList.value = NationInfoResponse(
//                        treasuryIncome = 0,
//                        averageStudentBalance = 0.0,
//                        activeItemCount = 0,
//                        classSavingsGoal = 0,
//                        isPossible = false // 국가 정보 없음
//                    )
//                }
            }
        }
    }

    private val _alarmList = MutableLiveData<List<AlarmDto>>() // LiveData를 활용
    val alarmList: LiveData<List<AlarmDto>> get() = _alarmList
    fun fetchAlarmList() {

    }




}