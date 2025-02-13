package com.ladysparks.ttaenggrang.data.model.response

import com.ladysparks.ttaenggrang.data.model.dto.StockTransactionDto

data class StockTransactionResponse(
    val data: StockTransactionDto?,  // ✅ `nullable` 처리 (data가 없을 수도 있음)
    val message: String,  // ✅ 응답 메시지 (예: "매도가 완료되었습니다.")
    val statusCode: Int  // ✅ 응답 상태 코드 (예: 0)
)