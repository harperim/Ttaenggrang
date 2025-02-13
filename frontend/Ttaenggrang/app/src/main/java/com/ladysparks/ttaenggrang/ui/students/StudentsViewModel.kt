package com.ladysparks.ttaenggrang.ui.students

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.AlarmDto
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.model.response.Teacher
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

class StudentsViewModel : ViewModel(){

    private val _studentList = MutableLiveData<List<StudentMultiCreateResponse>>() // LiveData를 활용
    val studentList: LiveData<List<StudentMultiCreateResponse>> get() = _studentList


    // 버튼이 클릭될 때 마다, 학생정보, 재정상태 여부를 판단해서 데이터를 불러오다.
    fun fetchStudentList() {
        viewModelScope.launch {
            runCatching {
                // 서버로부터 알림 데이터 요청
                RetrofitUtil.teacherService.getStudentList()
            }.onSuccess { response ->
                _studentList.value = response.data
            }.onFailure { exception ->
                Log.e("AlarmViewModel", "Error fetching alarms", exception)
            }
        }
    }
}