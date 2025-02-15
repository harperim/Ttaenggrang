package com.ladysparks.ttaenggrang.data.model.dto

import com.google.gson.annotations.SerializedName

data class BankTransactionDto(
    @SerializedName("type") val type: TransactionType,  // ✅ 거래 유형
    @SerializedName("amount") val amount: Int,         // ✅ 거래 금액
    @SerializedName("description") val description: String, // ✅ 거래 설명
    @SerializedName("receiverId") val receiverId: Int  // ✅ (선택) 받는 사람 ID
)

// ✅ 거래 유형 Enum (서버에서 요구하는 형식)
enum class TransactionType {
    DEPOSIT, WITHDRAW, TRANSFER, ITEM_BUY, ITEM_SELL, ITEM,
    STOCK_BUY, STOCK_SELL, ETF_BUY, ETF_SELL,
    SAVINGS_DEPOSIT, SAVINGS_INTEREST, BANK_INTEREST,
    SALARY, INCENTIVE, TAX, FINE
}