package com.ladysparks.ttaenggrang.data.model.dto

data class VoteDataDto(
    val id: Int? = null,
    val title: String,
    val startDate: String,
    val endDate: String,
    val voteMode: VoteMode,
    val voteStatus: VoteStatus,
    val students: Any? = null,
    val totalStudents: Int? = null,
    val totalVotes: Int? = null,
    val topRanks: List<VoteRankDto>? = null,
)

enum class VoteMode(val mode: String){
    STUDENT("STUDENT"),
    CUSTOM("CUSTOM")
}

enum class VoteStatus(val status: String){
    IN_PROGRESS("IN_PROGRESS "),
    COMPLETED ("COMPLETED ")
}