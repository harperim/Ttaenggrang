package com.ladysparks.ttaenggrang.data.model.dto

data class JobDto(
    val id: Int?,
    val jobName: String,
    val jobDescription: String,
    val baseSalary: Int,
    val maxPeople: Int,
    val salaryDate: String?,
    val students: Any? // ? 우선 job dto 에 맞게 구성
) {
    // 직업 생성시 필요한 보조 생성자
    constructor(
        jobName: String,
        jobDescription: String,
        baseSalary: Int,
        maxPeople: Int
    ) : this(null, jobName, jobDescription, baseSalary, maxPeople, null, null)
}