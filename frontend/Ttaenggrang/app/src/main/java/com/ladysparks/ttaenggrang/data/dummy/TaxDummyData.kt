package com.ladysparks.ttaenggrang.data.dummy

import com.ladysparks.ttaenggrang.data.model.dto.TaxDto


object TaxDummyData{
    val taxList = listOf(
        TaxDto(taxName = "소득세", taxRate = 0.1, taxDescription = "월급에서 공제되는 기본 소득세"),
        TaxDto(taxName = "자리 임대료",taxRate =0.05,taxDescription = "학급 자리 사용에 대한 임대료"),
        TaxDto(taxName = "건강 보험료",taxRate =0.03,taxDescription = "필수 건강 보험 공제"),
        TaxDto(taxName = "전기 요금",taxRate =0.02,taxDescription = "학급 전기 사용 요금"),
        TaxDto(taxName = "급식비",taxRate =0.015,taxDescription = "학급 급식비 공제"),
        TaxDto(taxName = "부가가치세",taxRate =0.05,taxDescription = "상품 및 서비스에 대한 부가가치세")
    )
}
