package com.ladysparks.ttaenggrang.data.model.dto

data class NationInfoDto(
    val id: Int? = null,
    val teacherId: Int? = null,
    val nationName : String,
    val population : Int,
    val currency : String, // 선생님 이름
    val savingsGoalAmount : Int,
    val establishedDate: String? = null,
    val nationalTreasury: Int? = null,
    val profileImageUrl: String? = null
)