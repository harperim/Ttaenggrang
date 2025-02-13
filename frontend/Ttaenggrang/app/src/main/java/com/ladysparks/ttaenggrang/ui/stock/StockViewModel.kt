package com.ladysparks.ttaenggrang.ui.stock

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.BankTransactionDto
import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.data.model.dto.StockTransactionDto
import com.ladysparks.ttaenggrang.data.model.dto.StudentStockDto
import com.ladysparks.ttaenggrang.data.model.dto.TransType
import com.ladysparks.ttaenggrang.data.model.dto.TransactionType
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil.Companion.bankService
import com.ladysparks.ttaenggrang.data.remote.StockService
import kotlinx.coroutines.launch

class StockViewModel : ViewModel() {
    private val stockService: StockService = RetrofitUtil.stockService

    //주식 전체조회
    private val _stockList = MutableLiveData<List<StockDto>>()
    val stockList: LiveData<List<StockDto>> get() = _stockList

    //주식 매도
    private val _sellTransaction = MutableLiveData<StockTransactionDto?>()
    val sellTransaction: LiveData<StockTransactionDto?> get() = _sellTransaction

    //주식 매수
    private val _buyTransaction = MutableLiveData<StockTransactionDto?>()
    val buyTransaction: LiveData<StockTransactionDto?> get() = _buyTransaction

    // 에러메세지
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    //  내 보유 주식 수 LiveData
    private val _ownedStocks = MutableLiveData<List<StudentStockDto>>()
    val ownedStocks: LiveData<List<StudentStockDto>> = _ownedStocks

    // 첫번째 아이템 불러오기
    private val _selectedStock = MutableLiveData<StockDto?>()
    val selectedStock: LiveData<StockDto?> get() = _selectedStock

    // 주식 열림 확인
    private val _isMarketActive = MutableLiveData<Boolean>()
    val isMarketActive: LiveData<Boolean> get() = _isMarketActive

    // 사용자가 입력한 거래 주식 수
    private val _tradeAmount = MutableLiveData<Int>()
    val tradeAmount: LiveData<Int> get() = _tradeAmount

    // 예상 결제 금액
    private val _expectedPayment = MutableLiveData<Int>()
    val expectedPayment: LiveData<Int> get() = _expectedPayment

    // 결제 후 내 자산 (임시 하드코딩된 자산 값 사용)
//    private val _myAsset = MutableLiveData<Int>(100000) // ✅ 현재 내 자산 (하드코딩된 값)
//    val myAsset: LiveData<Int> get() = _myAsset

    // 거래 후 내 보유 현금 계산
    private val _updatedBalance = MutableLiveData<Int>()
    val updatedBalance: LiveData<Int> get() = _updatedBalance

    // 거래 후 내 보유 주식 수
    private val _updatedOwnedStock = MutableLiveData<Int>()
    val updatedOwnedStock: LiveData<Int> get() = _updatedOwnedStock

    // 거래 가능 현금
    private val _balance = MutableLiveData<Int>()
    val balance: LiveData<Int> get() = _balance


    // 주식 데이터 조회
    fun fetchAllStocks() = viewModelScope.launch {
        runCatching {
            stockService.getAllStocks()
        }.onSuccess { stocks ->
            _stockList.postValue(stocks)
            if (stocks.isNotEmpty()) _selectedStock.postValue(stocks[0]) // 주식화면 로딩되면 바로 0번째 아이템을 노출
        }.onFailure { e ->
            Log.e("StockViewModel", "주식 목록 불러오기 실패", e)
        }
    }

    // 학생이 보유한 주식 목록 조회
    fun fetchOwnedStocks(studentId: Int) = viewModelScope.launch {
        runCatching {
            stockService.getStudentStocks(studentId)
        }.onSuccess { stocks ->
            _ownedStocks.postValue(stocks)
        }.onFailure { e ->
            Log.e("StockViewModel", "보유 주식 조회 실패", e)
            _ownedStocks.postValue(emptyList())
        }
    }

    // 주식 매도
    fun sellStock(stockId: Int, shareCount: Int, studentId: Int) = viewModelScope.launch {
        runCatching {
            stockService.sellStock(stockId, shareCount, studentId)
        }.onSuccess { response ->
            // 사용자가 입력한 값을 totalAmt에 넣음
            val transactionData = response.body()?.data
            // 데이터 동기화
            _sellTransaction.postValue(transactionData)
            fetchBalance()
        }.onFailure { e ->
            Log.e("StockViewModel", "매도 요청 실패", e)
            _errorMessage.postValue("매도 요청 실패: ${e.message}")
        }
    }

    // 매수 기능
    fun buyStock(stockId: Int, shareCount: Int, studentId: Int) = viewModelScope.launch {
        runCatching {
            stockService.buyStock(stockId, shareCount, studentId)
        }.onSuccess { response ->
            val transactionData = response.body()?.data
            //데이터 동기화
            _buyTransaction.postValue(transactionData)
            Log.d("StockViewModel", "매수 성공: ${response.body()?.data?.shareCount}주")
            fetchBalance()
        }.onFailure { e ->
            Log.e("StockViewModel", "매수 요청 실패", e)
            _errorMessage.postValue("매수 요청 실패: ${e.message}")
        }
    }

    // ✅ 특정 주식 선택 (리사이클러뷰에서 클릭 시 호출됨)
    fun selectStock(stock: StockDto) {
        _selectedStock.value = stock
    }

    //주식장 열기(교사)
    fun updateMarketStatus(openMarket: Boolean) = viewModelScope.launch {
        runCatching {
            stockService.setMarketStatus(openMarket)
        }.onSuccess { response ->
            _isMarketActive.postValue(response.body()?.data ?: false)
        }.onFailure {
            _isMarketActive.postValue(false)
        }
    }

    // 주식장 열림 확인(학생). 변경사항이 있을때만 ui 업데이트
    fun fetchMarketStatus() = viewModelScope.launch {
        runCatching {
            stockService.getMarketStatus()
        }.onSuccess { response ->
            val newStatus = response.body()?.data ?: false
            if (_isMarketActive.value != newStatus) {
                _isMarketActive.postValue(newStatus)
            }
        }.onFailure {
            _isMarketActive.postValue(false)
        }
    }

    // confirmDialog 에서 계산을 위한 뷰모델
    fun updateTradeAmount(amount: Int, stockPrice: Int, ownedStock: Int, transactionType: TransType) {
        _tradeAmount.postValue(amount)

        val calculatedPayment = stockPrice * amount
        _expectedPayment.postValue(calculatedPayment)

        _updatedBalance.postValue(
            if (transactionType == TransType.SELL) (_balance.value ?: 0) + calculatedPayment
            else (_balance.value ?: 0) - calculatedPayment
        )

        _updatedOwnedStock.postValue(
            if (transactionType == TransType.SELL) ownedStock - amount
            else ownedStock + amount
        )
    }

    // 주식 화면에 거래가능 현금 표시
    fun fetchBalance() = viewModelScope.launch {
        runCatching {
            bankService.getBankAccount()
        }.onSuccess { response ->
            Log.d("StockViewModel", "fetchBalance: ${response.body()?.data?.balance}")
            _balance.postValue(response.body()?.data?.balance ?: 0)
        }.onFailure { e ->
            Log.e("StockViewModel", "거래 가능 현금 조회 실패", e)
            _balance.postValue(0)
        }
    }
}



