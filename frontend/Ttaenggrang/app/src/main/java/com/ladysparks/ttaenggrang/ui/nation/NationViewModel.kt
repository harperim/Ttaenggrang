package com.ladysparks.ttaenggrang.ui.nation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.NationInfoDto
import com.ladysparks.ttaenggrang.data.model.dto.VoteDataDto
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.response.EconomySummaryResponse
import com.ladysparks.ttaenggrang.data.model.response.VoteCreateRequest
import com.ladysparks.ttaenggrang.data.model.response.VoteOptionResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.util.ApiErrorParser
import kotlinx.coroutines.launch

class NationViewModel : ViewModel(){
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // 국가 정보 불러오기
    private val _nationInfoList = MutableLiveData<NationInfoDto>()
    val nationInfoData: LiveData<NationInfoDto> get() = _nationInfoList
    fun fetchNationData(){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.teacherService.getNationInfo()
            }.onSuccess {
                _nationInfoList.value = it.data!!
            }.onFailure {
                Log.e("TAG", "fetchNationData Error: ${it}")
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)

            }
        }
    }

    // 현재 투표 정보 불러오기
    private val _currentVoteInfo = MutableLiveData<VoteDataDto>()
    val currentVoteInfo: LiveData<VoteDataDto> get() = _currentVoteInfo
    fun currentVoteInfo(){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.voteService.getCurrentVote()
            }.onSuccess {
                _currentVoteInfo.value = it.data!!
            }.onFailure {
                Log.e("TAG", "createVote: ${ApiErrorParser.extractErrorMessage(it)}", )
//                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }

    // 투표 생성
    private val _createVoteInfo = MutableLiveData<ApiResponse<VoteCreateRequest>>()
    val createVote: LiveData<ApiResponse<VoteCreateRequest>> get() = _createVoteInfo
    fun createVote(data: VoteCreateRequest){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.voteService.createVote(data)
            }.onSuccess {
                _createVoteInfo.value = _createVoteInfo.value
            }.onFailure {
                Log.e("TAG", "createVote: ${ApiErrorParser.extractErrorMessage(it)}", )
                // _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }

    // 투표 종료
    private val _endCurrentVote = MutableLiveData<ApiResponse<String>>()
    val endCurrentVote: LiveData<ApiResponse<String>> get() = _endCurrentVote
    fun endCurrentVote(){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.voteService.endCurrentVote()
            }.onSuccess {
                _endCurrentVote.value = it
            }.onFailure {
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }

    // 학생 리스트 불러오기 (투표할 때 목록 필요)
    private val _studentList = MutableLiveData<List<VoteOptionResponse>>()
    val studentList: LiveData<List<VoteOptionResponse>> get() = _studentList
    fun getStudentList(){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.voteService.getStudentList()
            }.onSuccess {
                _studentList.value = it.data ?: emptyList()
            }.onFailure {
                Log.d("TAG", "getStudentList: $ ApiErrorParser.extractErrorMessage(it)}" )
//                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }

    // 학생기능) 투표 참여
    private val _submitVoteData = MutableLiveData<ApiResponse<String>>()
    val submitVoteData: LiveData<ApiResponse<String>> get() = _submitVoteData
    fun submitVote(selectStudentId: Int){
        viewModelScope.launch {
            runCatching {
               RetrofitUtil.voteService.submitVote(selectStudentId)
            }.onSuccess {
                _submitVoteData.value = _submitVoteData.value
            }.onFailure {
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }


}