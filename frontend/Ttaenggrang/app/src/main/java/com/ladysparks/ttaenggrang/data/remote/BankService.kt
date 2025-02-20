package com.ladysparks.ttaenggrang.data.remote


import com.ladysparks.ttaenggrang.data.model.dto.BankHistoryDto
import com.ladysparks.ttaenggrang.data.model.dto.BankItemDto
import com.ladysparks.ttaenggrang.data.model.dto.BankManageDto
import com.ladysparks.ttaenggrang.data.model.dto.SavingSubscriptionDto
import com.ladysparks.ttaenggrang.data.model.request.SavingSubscriptionsRequest
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.response.BankAccountCountResponse
import com.ladysparks.ttaenggrang.data.model.response.BankAccountResponse
import com.ladysparks.ttaenggrang.data.model.response.SavingPayoutResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    // 은행 상품 전체 조회
    @GET("savings-products")
    suspend fun getBankItemAll(): Response<ApiResponse<List<BankItemDto>>>

    // 은행 상품 가입
//    @POST("savings-subscriptions")
//    suspend fun subscribeSavings(@Body request: SavingSubscriptionDto): ApiResponse<SavingSubscriptionDto>
    @POST("savings-subscriptions")
    suspend fun subscribeToSavings(@Body request: SavingSubscriptionsRequest): ApiResponse<SavingSubscriptionDto>



    // 적금 만기 받기
    @PATCH("savings-payouts/mature")
    suspend fun payoutSavings(
        @Query("savingsSubscriptionId") savingsSubscriptionId: Int
    ): ApiResponse<SavingPayoutResponse>


}


