package com.ladysparks.ttaenggrang.ui.students

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.data.model.request.JobRequest
import com.ladysparks.ttaenggrang.data.model.response.StockResponse
import com.ladysparks.ttaenggrang.data.model.response.StudentMultiCreateResponse
import com.ladysparks.ttaenggrang.data.model.response.StudentSavingResponse
import com.ladysparks.ttaenggrang.data.model.response.Teacher
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel
import com.ladysparks.ttaenggrang.util.ApiErrorParser
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class StudentsViewModel : ViewModel(){

    // 공통 : Error 처리
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // 학생 리스트
    private val _studentList = MutableLiveData<List<StudentMultiCreateResponse>?>() // LiveData를 활용
    val studentList: LiveData<List<StudentMultiCreateResponse>?> get() = _studentList
    fun fetchStudentList() {
        viewModelScope.launch {
            runCatching {
                // 서버로부터 알림 데이터 요청
                RetrofitUtil.teacherService.getStudentList()
            }.onSuccess { response ->
                _studentList.value = response.data
            }.onFailure { exception ->
                _errorMessage.value = ApiErrorParser.extractErrorMessage(exception)
                Log.e("AlarmViewModel", "Error fetchStudentList ", exception)
            }
        }
    }

    // 학생 등록 함수 (복수)
    val studentCount = MutableLiveData<Int>()
    val studentPrefix = MutableLiveData<String>()
    val uploadedFile = MutableLiveData<Uri?>()
    val uploadedFileName = MutableLiveData<String>() // 파일명을 저장할 LiveData
    val uploadedFileRequestBody = MutableLiveData<RequestBody?>()

    fun sendStudentDataToServer(context: Context) {
        val count = studentCount.value ?: 0
        val prefix = studentPrefix.value.orEmpty()
        val fileUri = uploadedFile.value

        if (count == 0 || prefix.isEmpty() || fileUri == null) {
            _errorMessage.value = "입력된 데이터가 부족합니다."
            Log.e("TAG", "sendStudentDataToServer: 파일이 선택되지 않았습니다.")
            return
        }

        viewModelScope.launch {
            runCatching {
                val filePart = createMultipartFile(context, fileUri)
                RetrofitUtil.teacherService.uploadStudentData(
                    baseId = prefix,
                    studentCount = count,
                    file = filePart
                )
            }.onSuccess {
                Log.d("StudentViewModel", "학생 정보 및 파일 전송 성공")
            }.onFailure {
                _errorMessage.value = "학생 정보 및 파일 전송 실패: ${it.message}"
                Log.e("TAG", "sendStudentDataToServer: ${it.message}")
            }
        }
    }

    private fun createMultipartFile(context: Context, uri: Uri): MultipartBody.Part {
        val file = FileUtils.getFileFromUri(context, uri)
        val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull()) // ✅ Content-Type 수정
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    fun uploadFile(uri: Uri, context: Context) {
        viewModelScope.launch {
            runCatching {
                val file = FileUtils.getFileFromUri(context, uri)
                uploadedFile.value = uri  // 🔹 파일 URI 저장
                uploadedFileName.value = file.name  // 🔹 파일명 저장
                uploadedFileRequestBody.value = createBinaryRequestBody(context, uri)
            }.onFailure {
                _errorMessage.value = "파일 업로드 실패: ${it.message}"
            }
        }
    }

    private fun createBinaryRequestBody(context: Context, uri: Uri): RequestBody {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("파일을 읽을 수 없습니다.")
        val byteArray = inputStream.readBytes()
        return byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())
    }

    // 학생 직업 수정
    private val _editStudentJob = MutableLiveData<Any>()
    val editStudentJob: LiveData<Any> get() = _editStudentJob
    fun editStudentJob(studentId: Int, jobId: Int) {
        viewModelScope.launch {
            runCatching {
                val request = JobRequest(jobId)
                RetrofitUtil.teacherService.editStudentJob(studentId, request) // ✅ API 호출
            }.onSuccess {
                _editStudentJob.value = it.data ?: ""
                Log.d("TAG", "editStudentJob: 수저 ok")
            }.onFailure {
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
                Log.d("TAG", "editStudentJob: 수저 fa")
            }
        }
    }

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
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
                Log.e("JobViewModel", "fetchJobList 33: Error FeatchJobList ${ApiErrorParser.extractErrorMessage(it)}" )
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

    // 은행 가입 상품 조회
    private val _savingList = MutableLiveData<List<StudentSavingResponse>>()
    val savingList: LiveData<List<StudentSavingResponse>> get() = _savingList
    fun userSavingSubscriptions(studentId: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.teacherService.userSavingSubscription(studentId)
            }.onSuccess {
                _savingList.value = it.data ?: emptyList()

            }.onFailure {
                Log.e("TAG", "userSavingSubscriptions 에러: ${it.message}")
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }

    // 주식 가입 상품 조회
    private val _stockList = MutableLiveData<List<StockResponse>>()
    val stockList: LiveData<List<StockResponse>> get() = _stockList
    fun stockList(studentId: Int){
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.teacherService.userStockList(studentId)
            }.onSuccess {
                _stockList.value = it.data ?: emptyList()
            }.onFailure {
                Log.e("TAG", "userSavingSubscriptions 에러: ${it.message}")
                _errorMessage.value = ApiErrorParser.extractErrorMessage(it)
            }
        }
    }

}