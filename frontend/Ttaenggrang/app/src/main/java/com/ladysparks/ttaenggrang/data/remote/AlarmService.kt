package com.ladysparks.ttaenggrang.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AlarmService {

    // POST 요청으로, teacherName 이 받은 알람 목록을 불러 온다
    @POST("rest/alarm")
    suspend fun saveAlarm(@Body teacherName: String): Boolean
}

