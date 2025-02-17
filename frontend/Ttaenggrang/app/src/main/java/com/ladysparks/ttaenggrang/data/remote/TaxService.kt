package com.ladysparks.ttaenggrang.data.remote

import com.ladysparks.ttaenggrang.data.model.dto.TaxDto
import com.ladysparks.ttaenggrang.data.model.request.TaxUseRequest
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.response.TaxNationHistoryResponse
import com.ladysparks.ttaenggrang.data.model.response.TaxStudentHistoryResponse
import com.ladysparks.ttaenggrang.data.model.response.TaxStudentPaymentAmountResponse
import com.ladysparks.ttaenggrang.data.model.response.TaxStudentTotalResponse
import com.ladysparks.ttaenggrang.data.model.response.TaxTeacherInfoResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface TaxService {
    // 교사 세금 전체 조회 - 확인 완료
    @GET("taxes/")
    suspend fun getTeacherTaxInfo(): ApiResponse<List<TaxTeacherInfoResponse>>

    // @Body multiStudent: StudentMultiCreateRequest 세금 등록 - 확인 완료
    @POST("taxes/")
    suspend fun registerTax(@Body tax: TaxDto): ApiResponse<Any>

    // 미납된 세금 일괄 납부
    @PUT("tax-payments/overdue/clear")
    suspend fun payOverdueTax(): ApiResponse<List<TaxStudentTotalResponse>>

    // 교사/학생이 본인 총 납부액 조회 - 확인 완료
    @GET("tax-payments/total-amount")
    suspend fun getStudentTaxAmount(@Query("studentId") studentId: Int?): ApiResponse<TaxStudentTotalResponse>

    // 교사의 전체 학생의 개별 세금 납부 총액 조회 - 확인 완료
    @GET("tax-payments/students")
    suspend fun getTeacherStudentTaxAmount(): ApiResponse<List<TaxStudentTotalResponse>>

    // 특정 학생의 기간에 따른 세금 납부 내역 조회(교사/학생) - 교사 완료
    @GET("tax-payments/period")
    suspend fun getStudentTaxPaymentsPeriod(
        @Query("studentId") studentId: Int? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): ApiResponse<List<TaxStudentHistoryResponse>>

    // 국세 사용 - 확인 완료
    @POST("taxes/use")
    suspend fun useTax(@Body eventUseTax: TaxUseRequest): ApiResponse<Unit>

    @GET("taxes/use")
    suspend fun getTaxHistory(): ApiResponse<List<TaxNationHistoryResponse>>


    @GET("tax-payments/teacher")
    suspend fun getStudentTaxPaymentsTeacher(): ApiResponse<List<TaxStudentPaymentAmountResponse>>

    @GET("tax-payments/overdue")
    suspend fun getOverDueTax(): ApiResponse<TaxStudentTotalResponse>





}