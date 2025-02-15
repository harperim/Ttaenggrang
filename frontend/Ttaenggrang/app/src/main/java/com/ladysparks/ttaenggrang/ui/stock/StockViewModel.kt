package com.ladysparks.ttaenggrang.ui.stock

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.NewsDto
import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.data.model.dto.StockTransactionDto
import com.ladysparks.ttaenggrang.data.model.dto.StockStudentDto
import com.ladysparks.ttaenggrang.data.model.dto.StockStudentTransactionDto
import com.ladysparks.ttaenggrang.data.model.dto.StockSummaryDto
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil.Companion.bankService
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil.Companion.stockService
import com.ladysparks.ttaenggrang.data.remote.StockService
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel
import com.ladysparks.ttaenggrang.util.ApiErrorParser
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import kotlinx.coroutines.launch

class StockViewModel : ViewModel() {
    private val stockService: StockService = RetrofitUtil.stockService

    //주식 전체조회
    private val _stockList = MutableLiveData<List<StockDto>?>()
    val stockList: MutableLiveData<List<StockDto>?> get() = _stockList

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
    private val _ownedStocks = MutableLiveData<List<StockStudentDto>?>()
    val ownedStocks: MutableLiveData<List<StockStudentDto>?> = _ownedStocks

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


    // 거래 후 내 보유 현금 계산
    private val _updatedBalance = MutableLiveData<Int>()
    val updatedBalance: LiveData<Int> get() = _updatedBalance

    // 거래 후 내 보유 주식 수
    private val _updatedOwnedStock = MutableLiveData<Int>()
    val updatedOwnedStock: LiveData<Int> get() = _updatedOwnedStock

    // 거래 가능 현금
    private val _balance = MutableLiveData<Int>()
    val balance: LiveData<Int> get() = _balance

    // 학생 주식 거래 기록
//    private val _stockTransaction = MutableLiveData<List<StockStudentTransactionDto>?>()
//    val stockTransaction: LiveData<List<StockStudentTransactionDto>> get() = _stockTransaction

    // 학생 주식 목록 테이블
    private val _stockTableData = MutableLiveData<List<BaseTableRowModel>>()
    val stockTableData: LiveData<List<BaseTableRowModel>> get() = _stockTableData

    // 총 투자액, 평가금액, 수익률 등의 요약 정보 LiveData 추가
    private val _stockSummary = MutableLiveData<Map<String, Any>>()
    val stockSummary: LiveData<Map<String, Any>> get() = _stockSummary

    // 이전 뉴스 기록 조회
    // 뉴스 생성
    private val _newsLiveData = MutableLiveData<NewsDto?>()
    val newsLiveData: LiveData<NewsDto?> get() = _newsLiveData

    // 로딩확인
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // 교사 주식 목록 조회
    private val _stockSummaryList = MutableLiveData<List<BaseTableRowModel>>()
    val stockSummaryList: LiveData<List<BaseTableRowModel>> get() = _stockSummaryList



    // 주식 데이터 조회
    fun fetchAllStocks() = viewModelScope.launch {
        runCatching {
            RetrofitUtil.stockService.getAllStocks()
        }.onSuccess { response ->
            _stockList.postValue(response.data)
            if (response.data?.isNotEmpty() == true) _selectedStock.postValue(response.data[0]) // 주식화면 로딩되면 바로 0번째 아이템을 노출
        }.onFailure { e ->
            Log.e("StockViewModel", "주식 목록 불러오기 실패", e)
        }
    }

    // 학생이 보유한 주식 목록 조회
    fun fetchOwnedStocks() = viewModelScope.launch {
        runCatching {
            stockService.getStocksStudent()
        }.onSuccess { response ->
            _ownedStocks.postValue(response.data)
            Log.d("TAG", "fetchOwnedStocks: 학생 주식 목록 조회성공!!!${response}")
        }.onFailure { e ->
            Log.e("StockViewModel", "보유 주식 조회 실패", e)
            _ownedStocks.postValue(emptyList())
        }
    }

    // 주식 매도
    fun sellStock(stockId: Int, shareQuantity: Int, studentId: Int) = viewModelScope.launch {
        runCatching {
            stockService.sellStock(stockId, shareQuantity, studentId)
        }.onSuccess { response ->
            // 데이터 동기화
            _sellTransaction.postValue(response.data)
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
            _buyTransaction.postValue(response.data)
            Log.d("StockViewModel", "매수 성공: ${response.data?.shareQuantity}주")
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
    fun updateTradeAmount(
        amount: Int,
        stockPrice: Int,
        ownedStock: Int,
        transactionType: String
    ) {
        _tradeAmount.postValue(amount)

        val calculatedPayment = stockPrice * amount
        _expectedPayment.postValue(calculatedPayment)

        _updatedBalance.postValue(
            if (transactionType == "SELL") (_balance.value ?: 0) + calculatedPayment
            else (_balance.value ?: 0) - calculatedPayment
        )

        _updatedOwnedStock.postValue(
            if (transactionType == "SELL") ownedStock - amount
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

//    // 학생 주식 거래 기록 조회
//    fun fetchStudentStockTransactions() = viewModelScope.launch {
//        runCatching {
//            stockService.getStockStudentTransaction()
//        }.onSuccess { response ->
//            Log.d("TAG", "fetchStudentStockTransactions: ${response.data}}")
//            // ✅ 개별 거래 로그 출력
//            response.data?.forEach { it ->
//                Log.d(
//                    "TAG", "거래 기록 - 학생ID: ${it.studentId}, " +
//                            "주식ID: ${it.stockId}, " +
//                            "거래유형: ${it.transactionType}, " +
//                            "거래수량: ${it.shareCount}, " +
//                            "거래날짜: ${it.transactionDate}, " +
//                            "매입가격: ${it.purchasePricePerShare}, " +
//                            "주식명: ${it.name}, " +
//                            "주식유형: ${it.type}"
//                )
//            }
//            _stockTransaction.postValue(response.data)
//        }.onFailure { e ->
//            Log.e("TAG", "fetchStudentStockTransactions 실패: ${e.message}", e)
//            // ✅ 실패 시 빈 리스트 반환하여 UI에서 처리 가능하도록 함
//            _stockTransaction.postValue(emptyList())
//
//        }
//    }
//
//    // 학생 주식 목록 테이블 계산
//    fun updateStockTableData(studentId: Int) {
//        val ownedStocks = ownedStocks.value ?: emptyList()
//        val transactions = stockTransaction.value ?: emptyList()
//
//        var totalInvestment = 0 // ✅ 총 투자액
//        var totalValuation = 0 // ✅ 총 평가금액
//
//        val newData = ownedStocks.map { stock ->
//            val matchingTransaction = transactions.find { it.stockId == stock.stockId }
//            val stockType = matchingTransaction?.stockType ?: "알 수 없음" // ✅ 주식 유형 가져오기
//
//            // ✅ 매수한 주식 수 합산 (보유 주식)
//            val totalShares = transactions
//                .filter { it.stockId == stock.stockId && it.transType == "BUY" } // 🔥 매수 거래만 필터링
//                .sumOf { it.shareCount }
//            Log.d("StockDebug", "주식 ${stock.stockName} - 매수한 주식 총 개수: $totalShares")
//
//            // ✅ 매수한 주식들의 총 매입 금액
//            val totalCost = transactions
//                .filter { it.stockId == stock.stockId && it.transType == "BUY" } // 🔥 매수 거래만 필터링
//                .sumOf { it.shareCount * it.purchasePrice }
//            Log.d("StockDebug", "주식 ${stock.stockName} - 총 매입 금액: $totalCost")
//
//            // ✅ 평균 매입 단가 계산 (총 매입 금액 / 총 매입 주식 수)
//            val avgPurchasePrice = if (totalShares > 0) totalCost / totalShares else 0
//            Log.d("StockDebug", "주식 ${stock.stockName} - 평균 매입 단가: $avgPurchasePrice")
//
//            // ✅ 평가금액 계산 (보유 주식 수 * 현재 주가)
//            val valuationAmount = stock.ownedQty * stock.currentPrice
//            Log.d("StockDebug", "주식 ${stock.stockName} - 평가금액: $valuationAmount")
//
//            // ✅ 손익금액 계산 (평가금액 - 투자금액)
//            val investmentAmount = stock.ownedQty * avgPurchasePrice
//            val profitLoss = valuationAmount - investmentAmount
//            Log.d("StockDebug", "주식 ${stock.stockName} - 손익금액: $profitLoss")
//
//            // ✅ 수익률 계산
//            val yield = if (investmentAmount > 0) {
//                (profitLoss.toFloat() / investmentAmount) * 100
//            } else 0f
//            Log.d("StockDebug", "주식 ${stock.stockName} - 수익률: %.2f%%".format(yield))
//
//            // ✅ 총 투자액과 총 평가금액 업데이트
//            totalInvestment += investmentAmount
//            totalValuation += valuationAmount
//
//            BaseTableRowModel(
//                listOf(
//                    stock.purchaseDate,      // 매수일
//                    stock.stockName,         // 주식명
//                    stockType,               // ✅ 주식 유형
//                    stock.ownedQty.toString(),  // 보유 주식 수
//                    avgPurchasePrice.toString(), // ✅ 평균 매입 단가
//                    stock.currentPrice.toString(), // 현재 주가
//                    valuationAmount.toString(), // ✅ 평가금액
//                    "%.2f%%".format(yield), // 수익률
//                    profitLoss.toString() // 손익금액
//                )
//            )
//        }

//        // ✅ 총 수익 & 총 수익률 계산
//        val totalProfit = totalValuation - totalInvestment
//        val totalReturnRate =
//            if (totalInvestment > 0) (totalProfit.toFloat() / totalInvestment) * 100 else 0f
//
//        Log.d("StockSummary", "총 투자액: $totalInvestment")
//        Log.d("StockSummary", "총 평가금액: $totalValuation")
//        Log.d("StockSummary", "총 수익: $totalProfit")
//        Log.d("StockSummary", "총 수익률: %.2f%%".format(totalReturnRate))
//
//        // ✅ 총 투자액, 평가금액, 수익률 LiveData 업데이트
//        _stockSummary.postValue(
//            mapOf(
//                "totalInvestment" to totalInvestment,
//                "totalValuation" to totalValuation,
//                "totalProfit" to totalProfit,
//                "totalReturnRate" to totalReturnRate
//            )
//        )
//        _stockTableData.postValue(newData)
//    }

    // 뉴스 생성
    fun createNews() {
        viewModelScope.launch {
            _isLoading.postValue(true) // 🔵 로딩 시작

            runCatching {
                stockService.createNews()
            }.onSuccess { response ->
                response.data?.let { news ->
                    _newsLiveData.postValue(null)
                    _newsLiveData.postValue(news) // 🟢 성공 시 데이터 업데이트
                }
            }.onFailure { error ->
                Log.e("StockViewModel", "뉴스 생성 실패: ${error.message}")
            }.also {
                _isLoading.postValue(false) // 🔴 로딩 종료
            }
        }
    }

    fun clearNewsData() {
        _newsLiveData.postValue(null)
    }

    // 교사 주식 목록 조회
    fun fetchStockList() {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.stockService.getStockList()
            }.onSuccess { response ->
                response.data?.let { stockList ->
                    Log.d("StockViewModel", "서버 응답 성공: $stockList") // ✅ 서버 응답 확인

                    // ✅ 뷰모델에서 BaseTableRowModel로 변환
                    val mappedData = stockList.map { stock ->
                        BaseTableRowModel(
                            listOf(
                                stock.createdDate,  // 등록일
                                stock.name,        // 종목명
                                stock.type,        // 주식 종류
                                stock.category,    // 카테고리
                                stock.pricePerShare.toString(), // 현재 가격
                                "${stock.priceChangeRate}%",  // 변동률
                                getTransactionEmoji(stock.transactionFrequency)  // 거래 활성도
                            )
                        )
                    }

                    _stockSummaryList.postValue(mappedData) // ✅ 변환된 데이터 저장
                }
            }.onFailure { error ->
                Log.e("StockViewModel", "fetchStockList 실패: ${error.message}")
                _errorMessage.postValue(ApiErrorParser.extractErrorMessage(error))
            }
        }
    }

    private fun getTransactionEmoji(frequency: Int): String {
        return when {
            frequency >= 50 -> "🔥🔥🔥"
            frequency in 20..49 -> "🔥🔥"
            else -> "🔥"
        }
    }

}






