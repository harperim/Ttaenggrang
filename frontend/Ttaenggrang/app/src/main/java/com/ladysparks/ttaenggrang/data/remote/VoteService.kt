package com.ladysparks.ttaenggrang.data.remote

import com.ladysparks.ttaenggrang.data.model.dto.VoteDataDto
import com.ladysparks.ttaenggrang.data.model.dto.VoteStatus
import com.ladysparks.ttaenggrang.data.model.request.TeacherSignUpRequest
import com.ladysparks.ttaenggrang.data.model.response.ApiResponse
import com.ladysparks.ttaenggrang.data.model.response.TeacherSignUpResponse
import com.ladysparks.ttaenggrang.data.model.response.VoteCreateRequest
import com.ladysparks.ttaenggrang.data.model.response.VoteOptionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VoteService {

    // 투표 현황 조회
    @GET("votes/current")
    suspend fun getCurrentVote(): ApiResponse<VoteDataDto>

    /*************** 학생 전용 Service ***************/
    @POST("votes/student") // 학생 투표 기능
    suspend fun submitVote(@Query("voteItemId") voteItemId: Int): ApiResponse<String>

    @GET("votes/list") //투표하기 위한 학생 목록 조회
    suspend fun getStudentList(): ApiResponse<List<VoteOptionResponse>>


    /*************** 교사 전용 Service ***************/

    @POST("votes/create")
    suspend fun createVote(@Body voteCreateData: VoteCreateRequest): ApiResponse<VoteCreateRequest>

    @POST("votes/stop")
    suspend fun endCurrentVote(): ApiResponse<String>
}