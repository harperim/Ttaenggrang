package com.ladysparks.ttaenggrang.data.remote

import com.ladysparks.ttaenggrang.data.model.request.StoreBuyingRequest
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.response.StoreStudenItemListResponse
import com.ladysparks.ttaenggrang.data.model.response.StoreStudentPurchaseHistoryResponse
import com.ladysparks.ttaenggrang.data.model.request.StoreRegisterRequest
import com.ladysparks.ttaenggrang.data.model.response.StoreMyAccountResponse
import com.ladysparks.ttaenggrang.data.model.response.StoreRegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface StoreService {
    // 학생/교사가 구매 가능한 아이템 전제 조회 - 실행 됨
    @GET("item-products")
    suspend fun getStudentItemList(): ApiResponse<List<StoreStudenItemListResponse>>

    // 학생의 구매 아이템 내역을 전체 조회 - 실행됨
    @GET("item-transactions/purchase")
    suspend fun getStudentPurchaseHistory(): ApiResponse<List<StoreStudentPurchaseHistoryResponse>>

    // 아이템 등록 - 실행 됨
    @POST("item-products")
    suspend fun registerItem(@Body registerProduct: StoreRegisterRequest): ApiResponse<StoreRegisterResponse>

    // 학생이 아이템 구매 - 실행됨
    @POST("item-transactions")
    suspend fun buyItem(@Body product: StoreBuyingRequest): ApiResponse<Any>

    // 아이템 개별 조회
    @POST("item-products/{itemId}")
    suspend fun getItemDetail(@Path("itemId")itemId: Int): ApiResponse<StoreRegisterResponse>


    // 아이템 사용 - 실행됨
    @PUT("item-transactions/{itemTransactionId}/use")
    suspend fun useItem(@Path("itemTransactionId")itemTransactionId: Int): ApiResponse<Any>

    @GET("bank-accounts")
    suspend fun getMyAccount(): ApiResponse<StoreMyAccountResponse>
}