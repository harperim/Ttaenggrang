package com.ladysparks.ttaenggrang.ui.stock

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.NewsDto
import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.data.model.dto.StockHistoryDto
import com.ladysparks.ttaenggrang.data.model.dto.StockTransactionDto
import com.ladysparks.ttaenggrang.data.model.dto.StockStudentDto

import com.ladysparks.ttaenggrang.data.model.dto.StockTransactionHistoryDto
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil.Companion.bankService
import com.ladysparks.ttaenggrang.data.remote.StockService
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel
import com.ladysparks.ttaenggrang.util.ApiErrorParser
import com.ladysparks.ttaenggrang.util.CustomDateUtil
import com.ladysparks.ttaenggrang.util.NumberUtil
import kotlinx.coroutines.delay
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
    private val _stockTransactionHistory = MutableLiveData<List<StockTransactionHistoryDto>?>()
    val stockTransactionHistory: MutableLiveData<List<StockTransactionHistoryDto>?> get() = _stockTransactionHistory

    // 학생 주식 목록 테이블
    private val _stockTableData = MutableLiveData<List<BaseTableRowModel>>()
    val stockTableData: LiveData<List<BaseTableRowModel>> get() = _stockTableData

    // 총 투자액, 평가금액, 수익률 등의 요약 정보 LiveData 추가
    private val _stockSummary = MutableLiveData<Map<String, Any>>()
    val stockSummary: LiveData<Map<String, Any>> get() = _stockSummary

    // 뉴스 전체 조회
    private val _newsListLiveData = MutableLiveData<List<NewsDto>?>()
    val newsListLiveData: MutableLiveData<List<NewsDto>?> get() = _newsListLiveData

    // 뉴스 생성, 저장
    private val _newsLiveData = MutableLiveData<NewsDto?>()
    val newsLiveData: LiveData<NewsDto?> get() = _newsLiveData

    // 뉴스 상세 조회
    private val _newsDetailLiveData = MutableLiveData<NewsDto?>()
    val newsDetailLiveData: LiveData<NewsDto?> get() = _newsDetailLiveData

    // 최신 뉴스 저장
    private val _latestNewsLiveData = MutableLiveData<NewsDto?>()
    val latestNewsLiveData: LiveData<NewsDto?> get() = _latestNewsLiveData

    // 로딩확인
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // 교사 주식 목록 조회
    private val _stockSummaryList = MutableLiveData<List<BaseTableRowModel>>()
    val stockSummaryList: LiveData<List<BaseTableRowModel>> get() = _stockSummaryList

    // 총 수익
    private val _totalProfit = MutableLiveData<Int>()
    val totalProfit: LiveData<Int> get() = _totalProfit

    // 총 수익률
    private val _totalYield = MutableLiveData<Float>()
    val totalYield: LiveData<Float> get() = _totalYield

    // 뉴스 그래프 조회
    private val _stockHistory = MutableLiveData<List<StockHistoryDto>>()
    val stockHistory: LiveData<List<StockHistoryDto>> get() = _stockHistory

    // 평균 매입 단가
    private val _avgPurchasePriceMap = MutableLiveData<Map<Int, Int>>()
    val avgPurchasePriceMap: LiveData<Map<Int, Int>> get() = _avgPurchasePriceMap

    // 내 주식 상세 다이얼로그에 띄울 데이터
    private val _selectedStockInfo = MutableLiveData<StockDetailInfo?>()
    val selectedStockInfo: LiveData<StockDetailInfo?> get() = _selectedStockInfo

    init {
        // ✅ 앱 실행 시 자동으로 거래 내역 가져와서 데이터 업데이트
        fetchStudentStockTransactions()
        fetchStockHistory()
    }


    // 전체 주식 목록 조회
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
            fetchStudentStockTransactions()
        }.onFailure { e ->
            Log.e("StockViewModel", "매도 요청 실패", e)
            _errorMessage.postValue("매도 요청 실패: ${e.message}")
        }
    }

    // 주식 매수
    fun buyStock(stockId: Int, shareCount: Int, studentId: Int) = viewModelScope.launch {
        runCatching {
            stockService.buyStock(stockId, shareCount, studentId)
        }.onSuccess { response ->
            _buyTransaction.postValue(response.data)
            Log.d("StockViewModel", "매수 성공: ${response.data?.shareQuantity}주")
            fetchBalance()
            fetchStudentStockTransactions()
        }.onFailure { e ->
            Log.e("StockViewModel", "매수 요청 실패", e)
            _errorMessage.postValue("매수 요청 실패: ${e.message}")
        }
    }

    // 특정 주식 선택 (리사이클러뷰에서 클릭 시 호출됨)
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


    // 학생 주식 거래 기록 조회
    fun fetchStudentStockTransactions() = viewModelScope.launch {
        runCatching {
            stockService.getStockTransactionHistory()
        }.onSuccess { response ->
            Log.d("TAG", "fetchStudentStockTransactions: ${response.data}}")
            // 개별 거래 로그 출력
            response.data?.forEach { it ->
                Log.d(
                    "TAG", "거래 기록 - 학생ID: ${it.studentId}, " +
                            "주식ID: ${it.stockId}, " +
                            "거래유형: ${it.transactionType}, " +
                            "거래수량: ${it.shareCount}, " +
                            "거래날짜: ${it.transactionDate}, " +
                            "매입가격: ${it.purchasePricePerShare}, " +
                            "주식명: ${it.name}, " +
                            "주식유형: ${it.type}"
                )
            }
            _stockTransactionHistory.postValue(response.data)
        }.onFailure { e ->
            Log.e("TAG", "fetchStudentStockTransactions 실패: ${e.message}", e)
            // ✅ 실패 시 빈 리스트 반환하여 UI에서 처리 가능하도록 함
            _stockTransactionHistory.postValue(emptyList())

        }
    }

    // 학생 주식 목록 테이블 계산
    fun updateStockTableData() {
        val transactions = stockTransactionHistory.value ?: emptyList()

        var totalInvestment = 0// ✅ 총 투자액
        var totalValuation = 0 // ✅ 총 평가금액

        val avgPriceMap = mutableMapOf<Int, Int>()

        // 🔹 거래 기록을 stockId 기준으로 그룹화
        val groupedTransactions = transactions.groupBy { it.stockId }

        // 🔹 거래 기록이 있는 주식 목록
        val transactionBasedStocks = groupedTransactions.map { (stockId, stockTransactions) ->
            val stockType = stockTransactions.firstOrNull()?.type ?: "알 수 없음"
            val stockName = stockTransactions.firstOrNull()?.name ?: "알 수 없음"
            val currentPrice = stockTransactions.firstOrNull()?.currentPrice ?: 0

            // 🔹 매수한 주식만 필터링하여 총 매입 금액과 총 매입 주식 수 계산
            val buyTransactions = stockTransactions.filter { it.transactionType == "BUY" }
            val totalShares = buyTransactions.sumOf { it.shareCount }
            val totalCost = buyTransactions.sumOf { it.shareCount * it.purchasePricePerShare }

            // 🔹 현재 보유 주식 수 계산 (BUY - SELL)
            val ownedShares = stockTransactions.sumOf {
                if (it.transactionType == "BUY") it.shareCount else -it.shareCount
            }

            // 🔹 평균 매입 단가 계산 (총 매입 금액 / 총 매입 주식 수) -> 정수형 변환
            val avgPurchasePrice = if (totalShares > 0) (totalCost / totalShares) else 0
            avgPriceMap[stockId] = avgPurchasePrice // ✅ 평균 매입 단가 저장
            _avgPurchasePriceMap.postValue(avgPriceMap)

            // 🔹 평가금액 계산 (보유 주식 수 * 현재 주가)
            val valuationAmount = ownedShares * currentPrice

            // 🔹 손익금액 계산 (평가금액 - 총 투자금액)
            val investmentAmount = ownedShares * avgPurchasePrice
            val profitLoss = valuationAmount - investmentAmount

            // 🔹 수익률 계산
            val yield = if (investmentAmount > 0) {
                (profitLoss.toFloat() / investmentAmount) * 100
            } else 0f

            // 🔹 총 투자액과 총 평가금액 업데이트
            totalInvestment += investmentAmount.toInt()
            totalValuation += valuationAmount.toInt()

            Log.d(
                "StockDebug",
                "주식: ${stockName}, 평균 매입 단가: $avgPurchasePrice, 수익률: $yield%, 손익금액: $profitLoss"
            )

            BaseTableRowModel(
                listOf(
                    //stockTransactions.firstOrNull()?.transactionDate ?: "",
                    stockTransactions.firstOrNull()?.transactionDate?.let { CustomDateUtil.formatToDate(it) } ?: "", // 매수일 (첫 거래 날짜)
                    stockName,              // 주식명
                    stockType,              // 주식 유형
                    "${ownedShares}주", // 보유 주식 수
                    NumberUtil.formatWithComma(avgPurchasePrice), // 평균 매입 단가
                    NumberUtil.formatWithComma(currentPrice) , // 현재 주가
                    NumberUtil.formatWithComma(valuationAmount), // 평가금액
                    "%.2f%%".format(yield), // 수익률
                    NumberUtil.formatWithComma(profitLoss) // 손익금액
                )
            )
        }
        // ✅ 총 수익 & 총 수익률 계산
        val totalProfit = totalValuation - totalInvestment
        val totalYield =
            if (totalInvestment > 0) (totalProfit.toFloat() / totalInvestment) * 100 else 0f

        _totalProfit.postValue(totalProfit.toInt()) // ✅ 총 수익 LiveData 업데이트
        _totalYield.postValue(totalYield) // ✅ 총 수익률 LiveData 업데이트

        Log.d("StockSummary", "총 투자액: $totalInvestment")
        Log.d("StockSummary", "총 평가금액: $totalValuation")
        Log.d("StockSummary", "총 수익: $totalProfit")
        Log.d("StockSummary", "총 수익률: %.2f%%".format(totalYield))

        // 총 투자액, 평가금액, 수익률 LiveData 업데이트
        _stockSummary.postValue(
            mapOf(
                "totalInvestment" to totalInvestment,
                "totalValuation" to totalValuation,
                "totalProfit" to totalProfit,
                "totalReturnRate" to totalYield
            )
        )
        _stockTableData.postValue(transactionBasedStocks)
        _totalYield.postValue(totalYield)
    }

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

    // 뉴스 저장
    fun addNews(newsDto: NewsDto) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.stockService.addNews(newsDto) // ✅ API 요청
            }.onSuccess { response ->
                _newsLiveData.postValue(response.data) // 🟢 성공 시 데이터 업데이트
                _newsLiveData.postValue(null)
                fetchNewsList()
                Log.d("NewsViewModel", "뉴스 저장 성공: ${response.data}")
            }.onFailure { error ->
                _errorMessage.postValue("네트워크 오류: ${error.message}")
                Log.e("NewsViewModel", "뉴스 저장 실패", error)
            }
        }
    }


    // 뉴스 전체 조회
    fun fetchNewsList() {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.stockService.getAllNews()
            }.onSuccess { response ->
                val newsList = response.data ?: emptyList()
                _newsListLiveData.postValue(newsList) // 성공 시 뉴스 리스트 업데이트
                if (newsList.isNotEmpty()) {
                    val latestNews = newsList.first() // 최신 뉴스 ID 가져오기

                    if (latestNewsId == null || latestNewsId != latestNews.id) {
                        latestNewsId = latestNews.id // 최신 뉴스 ID 업데이트
                        latestNews.id?.let { fetchNewsDetailForLatest(it) } // 최신 뉴스 상세 조회 실행
                    }
                }
                Log.d("fetchNewsList", "뉴스 목록 조회 성공: ${response.data?.size}건")
            }.onFailure { error ->
                _errorMessage.postValue("네트워크 오류: ${error.message}")
                Log.e("fetchNewsList", "뉴스 목록 조회 실패", error)
            }
        }
    }

    private var latestNewsId: Int? = null

    // ✅ 주기적으로 서버에서 최신 뉴스 확인 (Polling 방식)
    fun startNewsPolling() {
        viewModelScope.launch {
            while (true) { // 🔹 무한 루프 실행 (학생이 앱을 열고 있는 동안 계속 실행)
                checkForNewNews() // 🔹 새로운 뉴스가 있는지 확인
                delay(50000) // 🔹 5초마다 실행 (원하는 주기로 변경 가능)
            }
        }
    }

    // ✅ 새로운 뉴스가 있는지 확인하는 함수
    private suspend fun checkForNewNews() {
        runCatching {
            RetrofitUtil.stockService.getAllNews()
        }.onSuccess { response ->
            val newsList = response.data ?: emptyList()

            if (newsList.isNotEmpty()) {
                val newestNews = newsList.first() // 🔹 최신 뉴스 가져오기

                if (latestNewsId == null || latestNewsId != newestNews.id) { // 🔹 기존 뉴스 ID와 비교
                    latestNewsId = newestNews.id // 🔹 최신 뉴스 ID 업데이트
                    newestNews.id?.let { fetchNewsDetailForLatest(it) } // 🔹 최신 뉴스 상세 내용 가져오기
                }
            }
        }.onFailure { error ->
            Log.e("checkForNewNews", "뉴스 목록 조회 실패: ${error.message}")
        }
    }

    // 최신 뉴스의 상세 조회
    private fun fetchNewsDetailForLatest(newsId: Int) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.stockService.getNews(newsId)
            }.onSuccess { response ->
                response.data?.let { newsDetail ->
                    _latestNewsLiveData.postValue(newsDetail) // ✅ 최신 뉴스 업데이트 (content 포함)
                }
            }.onFailure { error ->
                Log.e("NewsViewModel", "최신 뉴스 상세 조회 실패: ${error.message}")
            }
        }
    }

    fun clearNewsDetail() {
        _newsDetailLiveData.value = null // ✅ LiveData 초기화
    }

    // 선택 뉴스 상세 조회
    fun fetchNewsDetail(newsId: Int) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.stockService.getNews(newsId)
            }.onSuccess { response ->
                response.data?.let {
                    _newsDetailLiveData.postValue(it) // ✅ 데이터 업데이트
                }
            }.onFailure { error ->
                Log.e("NewsViewModel", "뉴스 상세 조회 실패: ${error.message}")
                _errorMessage.postValue("뉴스를 불러오는데 실패했습니다.")
            }
        }
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

    // 주식 그래프 조회
    fun fetchStockHistory() {
        viewModelScope.launch {
            runCatching {
                stockService.getStockHistory() // ✅ API 요청
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.data?.values?.flatten()?.let { stockData ->
                        val sortedStockData = stockData.sortedBy { it.date }
                        _stockHistory.postValue(sortedStockData)

                        // ✅ 데이터 로그 확인
                        Log.d("ViewModel", "StockHistory 업데이트됨: ${sortedStockData.size}")
                    } ?: Log.d("ViewModel", "StockHistory 응답은 성공했지만 데이터 없음")
                } else {
                    Log.d("ViewModel", "StockHistory API 실패: ${response.code()}")
                }
            }.onFailure { e ->
                Log.e("ViewModel", "StockHistory API 호출 오류", e)
            }
        }
    }

    // ✅ 주식 상세 정보 설정 함수
    fun setSelectedStockInfo(stockId: Int) {
        val transactions = stockTransactionHistory.value ?: emptyList()
        val stockData = stockList.value?.find { it.id == stockId }
        val transactionHistory = transactions.filter { it.stockId == stockId }

        if (stockData == null || transactionHistory.isEmpty()) {
            _selectedStockInfo.postValue(null)
            return
        }

        val stockName = stockData.name
        val currentPrice = stockData.pricePerShare
        val changeRate = stockData.changeRate

        // ✅ 가장 최근 거래 날짜 찾기
        val purchaseDate = transactionHistory.minByOrNull { it.transactionDate }?.transactionDate
            ?.let { CustomDateUtil.formatToDate(it) } ?: "N/A"

        // ✅ 평균 매입 단가 가져오기
        val avgPurchasePrice = avgPurchasePriceMap.value?.get(stockId) ?: 0

        // ✅ 보유 주식 수 계산
        val ownedShares = transactionHistory.sumOf {
            if (it.transactionType == "BUY") it.shareCount else -it.shareCount
        }

        // ✅ 데이터 클래스 생성 후 LiveData 업데이트
        val stockDetailInfo = StockDetailInfo(
            stockId = stockId,
            stockName = stockName,
            currentPrice = currentPrice,
            changeRate = changeRate,
            purchaseDate = purchaseDate,
            avgPurchasePrice = avgPurchasePrice,
            ownedShares = ownedShares
        )
        _selectedStockInfo.postValue(stockDetailInfo)
    }
}
data class StockDetailInfo(
    val stockId: Int,
    val stockName: String,
    val currentPrice: Int,
    val changeRate: Int,
    val purchaseDate: String,
    val avgPurchasePrice: Int,
    val ownedShares: Int
)







