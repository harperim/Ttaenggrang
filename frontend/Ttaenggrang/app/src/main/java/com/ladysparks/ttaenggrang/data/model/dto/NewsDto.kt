package com.ladysparks.ttaenggrang.data.model.dto

data class NewsDto(
    val id: Int? = null,
    val content: String,
    val createdAt: String,
    val newsType: String,
    val stockName: String,
    val title: String
)