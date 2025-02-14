package com.ladysparks.ttaenggrang.data.model.response

data class VoteOptionResponse(
    val voteItemId: Int,
    val studentId: Int,
    val studentName: String,
    val profileImageUrl: Int,
    val voteCount: Int
)