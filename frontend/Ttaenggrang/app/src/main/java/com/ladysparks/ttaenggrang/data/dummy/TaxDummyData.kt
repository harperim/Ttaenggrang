package com.ladysparks.ttaenggrang.data.dummy

import com.ladysparks.ttaenggrang.data.model.dto.TaxDto


object TaxDummyData{
    val taxList = listOf(
        TaxDto("소득세",0.1,"월급에서 공제되는 기본 소득세"),
        TaxDto("자리 임대료",0.05,"학급 자리 사용에 대한 임대료"),
        TaxDto("건강 보험료",0.03,"필수 건강 보험 공제"),
        TaxDto("전기 요금",0.02,"학급 전기 사용 요금"),
        TaxDto("급식비",0.015,"학급 급식비 공제"),
        TaxDto("부가가치세",0.05,"상품 및 서비스에 대한 부가가치세")
    )
}
