package com.ladysparks.ttaenggrang.data.model.response

import com.ladysparks.ttaenggrang.data.model.dto.JobDto


// 학생 계정 생성 후 & 학생 전체 조회
data class StudentMultiCreateResponse(
    val id: Int?,
    val username: String,  // 학생 id
    val name: String?,     // 한글 학생 성명
    val profileImage: Any?,
    val teacher: Teacher,
    val bankAccount: BankAccountResponse?,
    val job: JobDto?,      // 학생 직업 정보
    val token: String,
)