package com.ladysparks.ttaenggrang.data.remote


import com.ladysparks.ttaenggrang.data.model.dto.BankTransactionDto
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.response.BankAccountResponse
import com.ladysparks.ttaenggrang.data.model.response.BankTransactionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BankService {
    // ✅ 사용자의 계좌 정보를 가져오는 API
    @GET("bank-accounts")
    suspend fun getBankAccount(): Response<ApiResponse<BankAccountResponse>>

    // ✅ 주식 거래 내역을 서버에 전송하고 응답 받기
    @POST("bank-transactions")
//    suspend fun sendBankTransaction(@Body transaction: BankTransactionDto): Response<BankTransactionResponse>
    suspend fun sendBankTransaction(@Body transaction: BankTransactionDto): Response<ApiResponse<BankTransactionDto>>
}


