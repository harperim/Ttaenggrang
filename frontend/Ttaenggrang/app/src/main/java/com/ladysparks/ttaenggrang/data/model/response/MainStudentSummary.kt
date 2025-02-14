package com.ladysparks.ttaenggrang.data.model.response

data class MainStudentSummary(
    val studentId: Int,
    val accountBalance: Int,
    val currentRank: Int, // 현재 자산 순위
    val totalSavings: Int, // 총 저축액
    val totalInvestmentAmount: Int, // 투자 평가액
    val totalAsset: Int, // 총 자산(적금 + 계좌 + 투자)
    val goalAmount: Int, // 목표저축액
    val achievementRate: Double, // 목표 달성률
)