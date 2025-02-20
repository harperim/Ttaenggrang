package com.example.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.BankHistoryDto
import com.ladysparks.ttaenggrang.data.model.dto.BankItemDto
import com.ladysparks.ttaenggrang.data.model.dto.BankManageDto
import com.ladysparks.ttaenggrang.data.model.dto.ProductItemDto
import com.ladysparks.ttaenggrang.data.model.dto.SavingSubscriptionDto
import com.ladysparks.ttaenggrang.data.model.request.SavingSubscriptionsRequest
import com.ladysparks.ttaenggrang.data.model.response.BankAccountCountResponse
import com.ladysparks.ttaenggrang.data.model.response.SavingPayoutResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil.Companion.bankService
import kotlinx.coroutines.launch

class BankViewModel : ViewModel() {

    // 적금 가입 내역을 담을 LiveData
    private val _savingsList = MutableLiveData<List<BankManageDto>>()
    val savingsList: LiveData<List<BankManageDto>> get() = _savingsList

    // 에러 메시지 관리
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // 학생 적금 개수 LiveData
    private val _bankAccountCount = MutableLiveData<BankAccountCountResponse?>()
    val bankAccountCount: LiveData<BankAccountCountResponse?> get() = _bankAccountCount

    // 적금 내역 LiveData
    private val _bankHistory = MutableLiveData<BankHistoryDto?>()
    val bankHistory: LiveData<BankHistoryDto?> get() = _bankHistory

    // 은행 상품 리스트
    private val _bankItemList = MutableLiveData<List<ProductItemDto?>?>()
    val bankItemList: MutableLiveData<List<ProductItemDto?>?> get() = _bankItemList

    // 메인화면에서 사용할 내 계좌 리스트
    private val _bankAccountList = MutableLiveData<List<Pair<BankManageDto, BankHistoryDto?>>>()
    val bankAccountList: LiveData<List<Pair<BankManageDto, BankHistoryDto?>>> get() = _bankAccountList

    // 적금 만기 지급 요청
    private val _payoutResult = MutableLiveData<SavingPayoutResponse?>()
    val payoutResult: LiveData<SavingPayoutResponse?> get() = _payoutResult

    // 은행 상품 가입
    private val _subscriptionResult = MutableLiveData<SavingSubscriptionDto?>()
    val subscriptionResult: LiveData<SavingSubscriptionDto?> get() = _subscriptionResult

    // active 상태 계좌의 총 납입금액을 합산(메인에 표시)
    private val _activeDepositTotal = MutableLiveData<Int>()
    val activeDepositTotal: LiveData<Int> get() = _activeDepositTotal

    // 가입 적금 전체 조회
    fun fetchUserSavings() {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.bankService.getUserSavings()
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        _savingsList.value = apiResponse.data ?: emptyList() // ✅ 리스트 그대로 할당
                        Log.d("BankViewModel", "적금 가입 내역 불러오기 성공: ${_savingsList.value}")
                    } ?: run {
                        _errorMessage.value = "데이터를 불러올 수 없습니다."
                        Log.e("BankViewModel", "적금 가입 내역이 null입니다.")
                    }
                } else {
                    _errorMessage.value = "서버 응답 오류: ${response.code()}"
                    Log.e("BankViewModel", "서버 응답 오류: ${response.code()}")
                }
            }.onFailure { e ->
                _errorMessage.value = "네트워크 오류: ${e.message}"
                Log.e("BankViewModel", "네트워크 오류: ${e.message}")
            }
        }
    }

    fun fetchUserSavingCount() {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.bankService.getUserSavingCount() // 네트워크 요청 실행
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        apiResponse.data?.let { data ->
                            _bankAccountCount.postValue(data) // ✅ postValue 사용
                            Log.d("BankViewModel", "적금 개수: ${data.savingsProductCount}, 예금 개수: ${data.depositProductCount}")
                        } ?: run {
                            _errorMessage.postValue("데이터를 불러올 수 없습니다.")
                            Log.e("BankViewModel", "예금/적금 개수가 null입니다.")
                        }
                    }
                } else {
                    _errorMessage.postValue("서버 응답 오류: ${response.code()}")
                    Log.e("BankViewModel", "서버 응답 오류: ${response.code()}")
                }
            }.onFailure { e ->
                _errorMessage.postValue("네트워크 오류: ${e.message}")
                Log.e("BankViewModel", "네트워크 오류: ${e.message}")
            }
        }
    }

    // 특정 적금 내역
    fun fetchBankHistory(savingsSubscriptionId: Int) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.bankService.getBankHistory(savingsSubscriptionId) // 네트워크 요청 실행
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        _bankHistory.postValue(apiResponse.data) // ✅ postValue 사용하여 업데이트
                        Log.d("BankHistoryViewModel", "적금 내역 불러오기 성공: $apiResponse")
                    } ?: run {
                        _errorMessage.postValue("데이터를 불러올 수 없습니다.")
                        Log.e("BankHistoryViewModel", "적금 내역이 null입니다.")
                    }
                } else {
                    _errorMessage.postValue("서버 응답 오류: ${response.code()}")
                    Log.e("BankHistoryViewModel", "서버 응답 오류: ${response.code()}")
                }
            }.onFailure { e ->
                _errorMessage.postValue("네트워크 오류: ${e.message}")
                Log.e("BankHistoryViewModel", "네트워크 오류: ${e.message}")
            }
        }
    }

    // 전체 은행 상품 조회
    fun fetchBankItems() {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.bankService.getBankItemAll() // ✅ API 호출
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    _bankItemList.postValue(response.body()?.data) // ✅ 성공 시 데이터 업데이트
                    Log.d("BankViewModel", "은행 상품 조회 성공: ${response.body()?.data?.size}건")
                } else {
                    _errorMessage.postValue("은행 상품을 불러오는데 실패했습니다.")
                    Log.e("BankViewModel", "은행 상품 조회 실패: ${response.body()?.message}")
                }
            }.onFailure { error ->
                _errorMessage.postValue("네트워크 오류: ${error.message}")
                Log.e("BankViewModel", "은행 상품 조회 실패", error)
            }
        }
    }

    // ✅ 내가 보유한 모든 적금 계좌 조회 (상품명, 총 납입 금액)
    fun fetchAllBankAccounts() {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.bankService.getUserSavings() // ✅ 내 보유 적금 목록 가져오기 (BankManageDto 리스트)
            }.onSuccess { response ->
                val bankManageList = response.body()?.data ?: emptyList()

                // ✅ 개별 상품의 savingName 가져오기
                val bankHistoryMap = mutableMapOf<Int, BankHistoryDto?>()

                bankManageList.forEach { bankManage ->
                    val historyResponse = runCatching {
                        RetrofitUtil.bankService.getBankHistory(bankManage.id) // ✅ 개별 적금의 상세 정보 가져오기
                    }.getOrNull()

                    bankHistoryMap[bankManage.id] = historyResponse?.body()?.data
                }

                // ✅ BankManageDto와 BankHistoryDto를 Pair로 묶어서 저장
                val pairedList = bankManageList.map { it to bankHistoryMap[it.id] }
                _bankAccountList.postValue(pairedList)

            }.onFailure { error ->
                _errorMessage.postValue("네트워크 오류: ${error.message}")
            }
        }
    }

    // 적금 만기 지급 요청
    fun requestPayout(savingsSubscriptionId: Int) {
        val existingPayout = _payoutResult.value
        if (existingPayout?.isPaid == true) {
            //_errorMessage.postValue("이미 지급된 적금입니다.")
            return
        }

        viewModelScope.launch {
            runCatching {
                bankService.payoutSavings(savingsSubscriptionId) // ✅ Query Parameter 사용
            }.onSuccess { response ->
                _payoutResult.postValue(response.data) // ✅ 성공 시 LiveData 업데이트
                Log.d("BankViewModel", "적금 만기 지급 성공: ${response.data}")
            }.onFailure { error ->
                _errorMessage.postValue("지급 요청 실패: ${error.message}")
                Log.e("BankViewModel", "적금 만기 지급 실패", error)
            }
        }
    }

    // 적금 가입
    fun subscribeToSavings(savingsProductId: Int, depositDayOfWeek: String) {
        viewModelScope.launch {
            runCatching {
                val request = SavingSubscriptionDto(depositDayOfWeek, savingsProductId)
                bankService.subscribeSavings(request)
            }.onSuccess { response ->
                _subscriptionResult.postValue(response.data)
                println("✅ 적금 가입 성공: $response")
            }.onFailure { error ->
                _errorMessage.postValue("적금 가입 실패: ${error.message}")
                println("❌ 적금 가입 실패: ${error.message}")
            }
        }
    }


    // ✅ "ACTIVE" 상태의 depositAmount 합산
    fun calculateActiveDepositTotal() {
        val activeTotal = savingsList.value
            ?.filter { it.status == "ACTIVE" }  // 🔹 "ACTIVE" 상태 필터링
            ?.sumOf { it.depositAmount } ?: 0   // 🔹 depositAmount 합산 (없으면 0)

        _activeDepositTotal.postValue(activeTotal) // 🔹 LiveData 업데이트
    }

    // 은행 차트 데이터
//    fun getTopSavingsForChart(bankAccounts: List<BankManageDto>): List<Pair<Float, String>> {
//        // ✅ 총 저축 금액 계산 (모든 계좌의 총 납입액 합)
//        val totalSavings = bankAccounts.sumOf { it.depositAmount }
//
//        if (totalSavings == 0) return emptyList() // ✅ 데이터 없으면 빈 리스트 반환
//
//        // ✅ 계좌별 총 납입액을 내림차순 정렬
//        val sortedAccounts = bankAccounts.sortedByDescending { it.depositAmount }
//
//        // ✅ 상위 2개 + 나머지를 '기타'로 그룹화
//        val topAccounts = sortedAccounts.take(2)
//        val otherTotal = sortedAccounts.drop(2).sumOf { it.depositAmount }
//
//        // ✅ PieChart에 표시할 데이터 변환 (Float 값으로 변환)
//        val chartData = topAccounts.map { account ->
//            Pair(account.depositAmount.toFloat(), "상품 ${account.savingsProductId}")
//        }.toMutableList()
//
//        if (otherTotal > 0) {
//            chartData.add(Pair(otherTotal.toFloat(), "기타")) // ✅ 나머지 계좌 합산
//        }
//
//        return chartData
//    }

    fun getTopSavingsForChart(bankManageList: List<BankManageDto>): List<Pair<Float, String>> {
        if (bankManageList.isEmpty()) {
            Log.e("PieChartDebug", "🚨 계좌 리스트가 비어 있음!")
            return emptyList()
        }

        // ✅ 총 저축 금액 계산
        val totalDepositAmount = bankManageList.sumOf { it.depositAmount}
        Log.d("PieChartDebug", "✅ 총 저축 금액: $totalDepositAmount")

        // ✅ 계좌별 총 납입액 내림차순 정렬 후 상위 2개 선택
        val sortedAccounts = bankManageList.sortedByDescending { it.depositAmount }
        val topAccounts = sortedAccounts.take(2)
        Log.d("PieChartDebug", "✅ 상위 2개 계좌: $topAccounts")

        // ✅ 기타 계좌(나머지 합산) 계산
        val otherAmount = totalDepositAmount - topAccounts.sumOf { it.depositAmount}
        Log.d("PieChartDebug", "✅ 기타 계좌 금액: $otherAmount")

        // ✅ 데이터 변환하여 반환 (상위 2개 + 기타 계좌)
        val chartData = topAccounts.map { Pair(it.depositAmount.toFloat(), it.savingsProductId.toString()) }.toMutableList()
        if (otherAmount > 0) {
            chartData.add(Pair(otherAmount.toFloat(), "기타"))
            Log.d("PieChartDebug", "✅ 기타 계좌 추가됨")
        }

        Log.d("PieChartDebug", "✅ 최종 변환된 차트 데이터: $chartData")
        return chartData
    }






}
