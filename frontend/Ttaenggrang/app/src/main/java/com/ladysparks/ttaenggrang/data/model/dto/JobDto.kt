package com.ladysparks.ttaenggrang.data.model.dto

// 보조 생성자가 많아질 경우 보조생성자 말고 null 처리 진행
data class JobDto(
    val id: Int?,
    val jobName: String,
    val jobDescription: String?,
    val baseSalary: Int,
    val maxPeople: Int?,
    val salaryDate: String? = null,
    val students: Any? = null, // ? 우선 job dto 에 맞게 구성
    val studentIds: List<Int>? = null
) {
    // 직업 생성시 필요한 보조 생성자
    constructor(
        jobName: String,
        jobDescription: String,
        baseSalary: Int,
        maxPeople: Int
    ) : this(null, jobName, jobDescription, baseSalary, maxPeople, null, null)

    // 서버 응답에서 jobName과 baseSalary만 있을 경우 사용할 보조 생성자
    constructor(
        jobName: String,
        baseSalary: Int
    ) : this(null, jobName, null, baseSalary, null, null, null)
}
