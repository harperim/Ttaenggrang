package com.ladysparks.ttaenggrang.data.remote


import com.ladysparks.ttaenggrang.data.model.dto.BankHistoryDto
import com.ladysparks.ttaenggrang.data.model.dto.BankManageDto
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.response.BankAccountCountResponse
import com.ladysparks.ttaenggrang.data.model.response.BankAccountResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BankService {
    // ✅ 사용자의 계좌 정보를 가져오는 API
    @GET("bank-accounts")
    suspend fun getBankAccount(): Response<ApiResponse<BankAccountResponse>>

    // 학생 적금 가입 내역
    @GET("savings-subscriptions")
    suspend fun getUserSavings(): Response<ApiResponse<List<BankManageDto>>> // ✅ `List<BankManageDto>`로 변경

    // 학생 적금 개수
    @GET("savings-subscriptions/savings-count")
    suspend fun getUserSavingCount(): Response<ApiResponse<BankAccountCountResponse>>

    //특정 적금 내역 조회
    @GET("savings-subscriptions/{savingsSubscriptionId}")
    suspend fun getBankHistory(
        @Path("savingsSubscriptionId") savingsSubscriptionId: Int
    ): Response<ApiResponse<BankHistoryDto>>
}


