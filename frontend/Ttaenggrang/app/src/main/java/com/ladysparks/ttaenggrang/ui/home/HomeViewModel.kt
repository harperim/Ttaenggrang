package com.ladysparks.ttaenggrang.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.AlarmDto
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.data.model.response.NationInfoResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.util.ApiErrorParser
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(){

    private val _nationInfoList = MutableLiveData<NationInfoResponse>()
    val nationInfoData: LiveData<NationInfoResponse> get() = _nationInfoList
    fun fetchNationData(){
        // 만약 국가 정복 없으면 등록으로 안내 해야함.
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.teacherService.getNationInfo()
            }.onSuccess {
                _nationInfoList.value = it.data
            }.onFailure {

            }
        }
    }

    private val _alarmList = MutableLiveData<List<AlarmDto>>() // LiveData를 활용
    val alarmList: LiveData<List<AlarmDto>> get() = _alarmList
    fun fetchAlarmList() {

    }




}