package com.ladysparks.ttaenggrang.ui.revenue

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.TaxDto
import com.ladysparks.ttaenggrang.data.model.request.TaxUseRequest
import com.ladysparks.ttaenggrang.data.model.response.TaxNationHistoryResponse
import com.ladysparks.ttaenggrang.data.model.response.TaxStudentHistoryResponse
import com.ladysparks.ttaenggrang.data.model.response.TaxStudentTotalResponse
import com.ladysparks.ttaenggrang.data.model.response.TaxTeacherInfoResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import kotlinx.coroutines.launch
import java.util.Date

class RevenueViewModel: ViewModel() {

    private val _studentTotalTaxInfo = MutableLiveData<TaxStudentTotalResponse>()
    val studentTotalTaxInfo: LiveData<TaxStudentTotalResponse> get() = _studentTotalTaxInfo

    fun fetchTaxStudentAmount(studentId: Int? = null) {

        // 학생 아이디가 없으면 요청하지 않음
        if (studentId == null) return

        viewModelScope.launch {
            runCatching {
                RetrofitUtil.taxService.getStudentTaxAmount(studentId)
            }.onSuccess {
                Log.d("fetchTaxStudentAmount Success", "success ${it}")
                _studentTotalTaxInfo.value = it.data ?: TaxStudentTotalResponse(0, "알 수 없음", 0)
            }.onFailure { throwable ->
                Log.e("fetchTaxStudentAmount Failure", "failure", throwable)
            }
        }
    }

    // 학생
    // 세금 납부 내역 조회(달 별로 볼 수 있다)
    // 미납 세금 납부하기
    // 국세 잔고 보기
    // 국세 내역 보기
    // 총 납부 금액
    // 이름


    // 선생님
    // 전체 세금 조회
    private val _taxList = MutableLiveData<List<TaxTeacherInfoResponse>>()
    val taxList: LiveData<List<TaxTeacherInfoResponse>> get() = _taxList

    fun fetchTaxList() {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.taxService.getTeacherTaxInfo()
            }.onSuccess {
                _taxList.value = it.data?: emptyList()
                Log.d("fetchTaxList Success", "success ${it}")
            }.onFailure { throwable ->
                Log.e("fetchTaxList Failure", "failure", throwable)
            }
        }
    }

    // 세금 등록하기
    fun registerTax(taxDto: TaxDto, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.taxService.registerTax(taxDto)
            }.onSuccess {
                Log.d("registerTax Success", "세금 등록 성공")
                onSuccess()
            }.onFailure { throwable ->
                Log.e("registerTax Failure", "세금 등록 실패", throwable)
                onFailure()
            }
        }
    }

    private val _studentTaxAmountList = MutableLiveData<List<TaxStudentTotalResponse>>()
    val studentTaxAmountList : LiveData<List<TaxStudentTotalResponse>>get() = _studentTaxAmountList

    //  선생님이 전체 학생 대상 각각 항생 납부 총액 조회
    fun fetchTeacherStudentPaymentsAmount() {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.taxService.getTeacherStudentTaxAmount()
            }.onSuccess {
                _studentTaxAmountList.value = it.data?: emptyList()
                Log.d("fetchStudentTaxAmountList Success", "success ${it}")
            }.onFailure { throwable ->
                Log.d("fetchStudentTaxAmountList Failure", "fail", throwable)
            }
        }
    }

    private val _studentTaxHistory = MutableLiveData<List<TaxStudentHistoryResponse>>()
    val studentTaxHistory : LiveData<List<TaxStudentHistoryResponse>>get() = _studentTaxHistory

    //  특정 학생의 기간에 따른 세금 납부 내역 조회(교사/학생)
    fun fetchStudentTaxHistory(studentId: Int, startDate: String, endDate: String) {

        viewModelScope.launch {
            runCatching {
                RetrofitUtil.taxService.getStudentTaxPaymentsPeriod(studentId, startDate, endDate)
            }.onSuccess {
                _studentTaxHistory.value = it.data?: emptyList()
                Log.d("fetchStudentTaxHistory Success", "success ${it}")
            }.onFailure { throwable ->
                Log.d("fetchStudentTaxHistory Failure", "fail", throwable)
            }
        }
    }

    //  국세 사용
    fun useTax(name: String, amount: Int, description: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val eventUseTax =  TaxUseRequest(
            name = name,
            amount = amount,
            description = description
        )


        viewModelScope.launch {
            runCatching {
                RetrofitUtil.taxService.useTax(eventUseTax)
            }.onSuccess {
                Log.d("useTax Success", "success ${it}")
                onSuccess()
            }.onFailure { throwable ->
                Log.e("useTax Failure", "Failure:", throwable)
                onFailure()
            }
        }
    }

    private val _nationTaxHistory = MutableLiveData<List<TaxNationHistoryResponse>>()
    val nationTaxHistory : LiveData<List<TaxNationHistoryResponse>>get() = _nationTaxHistory

    //  국세 사용 내역
    fun fetchNationTaxHistory() {

        viewModelScope.launch {
            runCatching {
                RetrofitUtil.taxService.getTaxHistory()
            }.onSuccess {
                Log.d("fetchNationTaxHistory Success", "success ${it}")
                _nationTaxHistory.value = it.data ?: emptyList()
            }.onFailure { throwable ->
                Log.e("getNationTaxHistory Failure", "Failure:", throwable)
            }
        }
    }


}