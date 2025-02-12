package com.ladysparks.ttaenggrang.data.remote

import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.data.model.dto.StockTransactionDto
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.response.StockOpenResponse
import com.ladysparks.ttaenggrang.data.model.response.StockTransactionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StockService {

    // 주식 매도
    @POST("stocks/{stockId}/sell")  // ✅ 경로 확인
    suspend fun sellStock(
        @Path("stockId") stockId: Long,
        @Query("share_count") shareCount: Int,
        @Query("studentId") studentId: Long
    ): Response<StockTransactionResponse>

    // 주식 매수
    @POST("stocks/{stockId}/buy")  // ✅ 경로 확인
    suspend fun buyStock(
        @Path("stockId") stockId: Long,
        @Query("share_count") shareCount: Int,
        @Query("studentId") studentId: Long
    ): Response<StockTransactionResponse>


    // 주식장 열기 (교사)
    @POST("stocks/open")
    suspend fun openMarket(): ApiResponse<StockOpenResponse>

    // 주식 전체 조회
    @GET("stocks")
    suspend fun getAllStocks(): List<StockDto>

}