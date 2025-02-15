package com.ladysparks.ttaenggrang.data.remote

import com.ladysparks.ttaenggrang.data.model.dto.NewsDto
import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.data.model.dto.StockStudentDto
import com.ladysparks.ttaenggrang.data.model.dto.StockSummaryDto
import com.ladysparks.ttaenggrang.data.model.dto.StockTransactionDto
import com.ladysparks.ttaenggrang.data.model.dto.StockTransactionHistoryDto
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.response.OpenMarketResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StockService {

    // 주식 매도
    @POST("stock-transactions/sell/{stockId}")
    suspend fun sellStock(
        @Path("stockId") stockId: Int,
        @Query("share_count") shareCount: Int,
        @Query("studentId") studentId: Int
    ): ApiResponse<StockTransactionDto>

    // 주식 매수
    @POST("stock-transactions/buy/{stockId}")  // ✅ 경로 확인
    suspend fun buyStock(
        @Path("stockId") stockId: Int,
        @Query("share_count") shareCount: Int,
        @Query("studentId") studentId: Int
    ): ApiResponse<StockTransactionDto>

    // 주식 전체 조회
    @GET("stocks")
    suspend fun getAllStocks(): ApiResponse<List<StockDto>>

    //주식 상세 조회
//    @GET("stocks/{stockId}")
//    suspend fun getStock(
//        @Path("stockId") stockId: Int
//    ):  Response<>

    //주식장 열림(교사)
    @POST("stocks/manage")
    suspend fun setMarketStatus(
        @Query("openMarket") openMarket: Boolean // true 또는 false 값을 보냄
    ): Response<OpenMarketResponse>

    //주식장 열림 확인(학생)
    @GET("stocks/status")
    suspend fun getMarketStatus(): Response<OpenMarketResponse>

    // 학생 보유 주식 조회
    @GET("stocks/buy")
    suspend fun getStocksStudent(
    ): ApiResponse<List<StockStudentDto>>

    // 학생 주식 거래 조회
    @GET("stock-transactions")
    suspend fun getStockTransactionHistory(
    ): ApiResponse<List<StockTransactionHistoryDto>>

    // 뉴스 생성
    @POST("news/create")
    suspend fun createNews(
    ): ApiResponse<NewsDto>

    // 교사 주식 목록 조회
    @GET("stocks/summary")
    suspend fun getStockList(
    ): ApiResponse<List<StockSummaryDto>>


}