package com.ladysparks.ttaenggrang.base

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
//import com.github.mikephil.charting.BuildConfig
import com.ladysparks.ttaenggrang.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ladysparks.ttaenggrang.realm.NotificationModel
import com.ladysparks.ttaenggrang.realm.NotificationRepository
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {
    companion object {

        // SERVER_URL 참조 오류 발생할 경우, import 패키지 경로 확인해 볼 것.
        const val SERVER_URL = BuildConfig.SERVER_URL
        const val IMGS_URL = ""

        lateinit var retrofit: Retrofit
        lateinit var realm: Realm
    }

    override fun onCreate() {
        super.onCreate()

        // FCM Channel
        createNotificationChannel()

        // SharedPreferencesUtil
        SharedPreferencesUtil.init(this)

        // Realm 초기화 추가
        val config = RealmConfiguration.Builder(schema = setOf(NotificationModel::class))
            .schemaVersion(3) // 스키마 버전 설정
            .build()
        realm = Realm.open(config) // Realm 전역 인스턴스 생성

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 레트로핏 인스턴스를 생성하고, 레트로핏에 각종 설정값들을 지정해줍니다.
        // 연결 타임아웃시간은 5초로 지정이 되어있고, HttpLoggingInterceptor를 붙여서 어떤 요청이 나가고 들어오는지를 보여줍니다.
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(logging) // 로그 확인
            .addInterceptor(AddAuthInterceptor()) // JWT 토큰 추가
            .build()

        // 앱이 처음 생성되는 순간, retrofit 인스턴스를 생성
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    val gson : Gson = GsonBuilder()
        .setLenient()
        .setPrettyPrinting()  // JSON을 보기 좋게 출력
        .create()


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "ttaenggrang_fcm_default_channel"
            val channelName = "FCM ttaenggrang"
            val descriptionText = "Firebase Cloud Messaging Default Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

}