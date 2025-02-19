package com.ladysparks.ttaenggrang.util

object TransactionTypeUtil {
    fun convertToKorean(type: String): String {
        return when (type) {
            "DEPOSIT" -> "입금"
            "WITHDRAW" -> "출금"
            "TRANSFER" -> "송금"
            "ITEM_BUY" -> "아이템 구매"
            "STOCK_BUY" -> "주식 매수"
            "STOCK_SELL" -> "주식 매도"
            "ETF_BUY" -> "ETF 매수"
            "ETF_SELL" -> "ETF 매도"
            "SAVINGS_DEPOSIT" -> "적금 납입"
            "SAVINGS_INTEREST" -> "적금 이자 수령"
            "BANK_INTEREST" -> "은행 계좌 이자 수령"
            "SALARY" -> "급여 수령"
            "INCENTIVE" -> "인센티브 수령"
            "TAX" -> "세금 납부"
            "FINE" -> "벌금 납부"
            else -> "알 수 없는 거래" // 예외 처리 (서버에서 정의되지 않은 타입일 경우)
        }
    }
}