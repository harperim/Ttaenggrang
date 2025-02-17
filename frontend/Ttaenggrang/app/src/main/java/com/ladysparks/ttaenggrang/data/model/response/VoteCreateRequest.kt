package com.ladysparks.ttaenggrang.data.model.response

import com.ladysparks.ttaenggrang.data.model.dto.VoteMode

data class VoteCreateRequest(
    val title: String,
    val startDate: String,
    val endDate: String,
    val voteMode: VoteMode
)