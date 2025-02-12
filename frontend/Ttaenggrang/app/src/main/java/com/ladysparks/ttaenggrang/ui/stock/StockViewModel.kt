package com.ladysparks.ttaenggrang.ui.stock

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.data.model.dto.StockTransactionDto
import com.ladysparks.ttaenggrang.data.model.request.StudentSignInRequest
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil.Companion.authService
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
    private val _ownedStockQty = MutableLiveData<Int>()
    val ownedStockQty: LiveData<Int> get() = _ownedStockQty

    // 첫번째 아이템 불러오기
    private val _selectedStock = MutableLiveData<StockDto?>()
    val selectedStock: LiveData<StockDto?> get() = _selectedStock


    // 주식 데이터 조회
    fun fetchAllStocks() {
        viewModelScope.launch {
            try {
                val stocks = stockService.getAllStocks()
                _stockList.postValue(stocks)

                //  주식 목록을 불러온 후 첫 번째 주식을 선택
                if (stocks.isNotEmpty()) {
                    _selectedStock.postValue(stocks[0])
                }

            } catch (e: Exception) {
                Log.e("StockViewModel", "주식 목록 불러오기 실패", e)
            }
        }
    }

    // 학생 ID 조회 후 보유 주식 수 가져오기
    fun fetchOwnedStockQty(stockId: Long) {

        viewModelScope.launch {
            try {
                // ✅ 서버에서 로그인된 학생 ID 가져오기
                val loginResponse = authService.loginStudent(
                    StudentSignInRequest(username = "hello1", password = "ssafy123") // 🔴 실제 로그인 정보로 변경 필요
                )

                if (loginResponse.statusCode == 200) {
                    val studentId = loginResponse.data?.id?.toLong()
                        ?: throw IllegalStateException("학생 ID 없음")

                    Log.d("StockViewModel", "서버에서 가져온 studentId: $studentId")

                    // ✅ 주식 매도 API를 통해 내 보유 주식 조회 (매도하지 않고 조회만 진행)
                    val sellResponse = stockService.sellStock(stockId, 0, studentId) // ✅ 0주 매도로 정보만 가져옴

                    if (sellResponse.isSuccessful) {
                        val transactionData = sellResponse.body()?.data
                        _ownedStockQty.postValue(transactionData?.ownedQty ?: 0) // ✅ 보유 주식 업데이트
                    } else {
                        _errorMessage.postValue("보유 주식 조회 실패 (HTTP ${sellResponse.code()})"

                        )
                    }
                } else {
                    _errorMessage.postValue("학생 정보 조회 실패")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("네트워크 오류 발생: ${e.message}")
                Log.e("StockViewModel", "네트워크 오류", e)
            }
        }
    }

    // 학생 ID 조회 후 주식 매도 요청
    fun sellStock(stockId: Long, shareCount: Int) {
        viewModelScope.launch {
            try {
                // ✅ 서버에서 로그인된 학생 ID 가져오기
                val loginResponse = authService.loginStudent(
                    StudentSignInRequest(username = "hello1", password = "ssafy123") // 🔴 실제 로그인 정보로 변경 필요
                )

                if (loginResponse.statusCode == 200) {
                    val studentId = loginResponse.data?.id?.toLong()
                        ?: throw IllegalStateException("학생 ID 없음")

                    Log.d("StockViewModel", "서버에서 가져온 studentId: $studentId")

                    // ✅ `sellStock()` 요청 실행
                    val sellResponse = stockService.sellStock(stockId, shareCount, studentId)

                    if (sellResponse.isSuccessful) {
                        val transactionData = sellResponse.body()?.data

                        // ✅ 매도 성공 후 보유 주식 수 업데이트
                        _sellTransaction.postValue(transactionData)
                        _ownedStockQty.postValue(transactionData?.ownedQty ?: 0)

                    } else {
                        _errorMessage.postValue("매도 요청 실패 (HTTP ${sellResponse.code()})")
                    }
                } else {
                    _errorMessage.postValue("학생 정보 조회 실패")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("네트워크 오류 발생: ${e.message}")
                Log.e("StockViewModel", "네트워크 오류", e)
            }
        }
    }

    // 매수 기능
    fun buyStock(stockId: Long, shareCount: Int) {
        viewModelScope.launch {
            try {
                // ✅ 서버에서 로그인된 학생 ID 가져오기
                val loginResponse = authService.loginStudent(
                    StudentSignInRequest(username = "hello1", password = "ssafy123") // 🔴 실제 로그인 정보로 변경 필요
                )

                if (loginResponse.statusCode == 200) {
                    val studentId = loginResponse.data?.id?.toLong()
                        ?: throw IllegalStateException("학생 ID 없음")

                    Log.d("StockViewModel", "서버에서 가져온 studentId: $studentId")

                    // ✅ `buyStock()` 요청 실행
                    val buyResponse = stockService.sellStock(stockId, shareCount, studentId)

                    if (buyResponse.isSuccessful) {
                        val transactionData = buyResponse.body()?.data

                        // ✅ 매수 성공 후 보유 주식 수 업데이트
                        _buyTransaction.postValue(transactionData)
                        _ownedStockQty.postValue(transactionData?.ownedQty ?: 0)

                    } else {
                        _errorMessage.postValue("매수 요청 실패 (HTTP ${buyResponse.code()})")
                    }
                } else {
                    _errorMessage.postValue("학생 정보 조회 실패")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("네트워크 오류 발생: ${e.message}")
                Log.e("StockViewModel", "네트워크 오류", e)
            }
        }
    }

    // ✅ 특정 주식 선택 (리사이클러뷰에서 클릭 시 호출됨)
    fun selectStock(stock: StockDto) {
        _selectedStock.value = stock
    }


}


//class StockViewModel(private val apiService: ApiService) : ViewModel() {
//
//    private val _stockList = MutableLiveData<List<StockDto>>()
//    val stockList: LiveData<List<StockDto>> get() = _stockList
//
//    private val _selectedStock = MutableLiveData<StockDto?>()
//    val selectedStock: LiveData<StockDto?> get() = _selectedStock
//
//    private val _ownedStockQty = MutableLiveData<Int>()
//    val ownedStockQty: LiveData<Int> get() = _ownedStockQty
//
//    // 주식 목록 가져오기
//    fun fetchStockList() {
//        viewModelScope.launch {
//            try {
//                val response = apiService.getStockList()
//                _stockList.postValue(response)
//            } catch (e: Exception) {
//                Log.e("StockViewModel", "주식 목록 불러오기 실패", e)
//            }
//        }
//    }
//
//    // 특정 주식 선택
//    fun selectStock(stock: StockDto) {
//        _selectedStock.value = stock
//        fetchOwnedStockQty(stock.id)
//    }
//
//    // 사용자가 보유한 주식 개수 가져오기
//    private fun fetchOwnedStockQty(stockId: Int) {
//        viewModelScope.launch {
//            try {
//                val response = apiService.getOwnedStockQty(stockId)
//                _ownedStockQty.postValue(response)
//            } catch (e: Exception) {
//                Log.e("StockViewModel", "보유 주식 개수 불러오기 실패", e)
//                _ownedStockQty.postValue(0) // 실패 시 0으로 설정
//            }
//        }
//    }
//
//    // 주식 매수
//    fun buyStock(stockId: Int, qty: Int) {
//        viewModelScope.launch {
//            try {
//                val response = apiService.buyStock(
//                    StockTransactionDto(
//                        stockId = stockId,
//                        shareCount = qty,
//                        transType = TransType.BUY
//                    )
//                )
//                if (response) {
//                    fetchStockList()
//                    fetchOwnedStockQty(stockId)
//                }
//            } catch (e: Exception) {
//                Log.e("StockViewModel", "매수 실패", e)
//            }
//        }
//    }
//
//    // 주식 매도
//    fun sellStock(stockId: Int, qty: Int) {
//        viewModelScope.launch {
//            try {
//                val response = apiService.sellStock(
//                    StockTransactionDto(
//                        stockId = stockId,
//                        shareCount = qty,
//                        transType = TransType.SELL
//                    )
//                )
//                if (response) {
//                    fetchStockList()
//                    fetchOwnedStockQty(stockId)
//                }
//            } catch (e: Exception) {
//                Log.e("StockViewModel", "매도 실패", e)
//            }
//        }
//    }
//}
//
//
