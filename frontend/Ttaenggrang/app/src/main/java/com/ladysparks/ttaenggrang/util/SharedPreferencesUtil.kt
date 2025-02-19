package com.ladysparks.ttaenggrang.util

import android.content.Context
import android.content.SharedPreferences

/**
 * [사용 방법]
 * - 다양한 데이터 유형 저장/관리를 지원하기 위하 '오버로딩' 방식 사용하여 구현
 * - String, Int, boolean, float, long 지원
 *
 * 저장
 * SharedPreferencesUtil.putValue(context, "user_name", "Alice")
 * SharedPreferencesUtil.putValue(context, "user_age", 25)
 *
 * 데이터 확인
 * - getValue(context, 이름, 값이 없을 경우 보여질 기본 값) 구조
 * val name = SharedPreferencesUtil.getValue(context, "user_name", "Unknown")
 * val age = SharedPreferencesUtil.getValue(context, "user_age", 0)
 *
 * 삭제
 * - 삭제는 반환 값 true/false 제공
 * - 만약 삭제할 데이터(key) 존재하지 않을 경우 false 를 반환한다.gi
 * val isRemoved = SharedPreferencesUtil.removeValue(context, "user_age")
 *
 * 초기화
 * SharedPreferencesUtil.clearAll(context)
 */
object SharedPreferencesUtil {
    private const val PREFS_NAME = "app_prefs"
    private const val COOKIES_KEY_NAME = "cookies"

    // Key 상수 값 모음
    const val JWT_TOKEN_KEY = "jwt_token"
    const val IS_TEACHER = "is_teacher"
    const val USER_ACCOUNT = "user_email"
    const val USER_ID = "user_id" // ✅ 사용자 ID 저장 키 추가
    const val VOTE_HISTORY_ID = "vote_histor_id" // 최근 참여한 투표 id
    const val IS_EVENT_SHOW = "is_event_show" // 적금만기 알람을 받았는지
    const val MY_RANK = "my_rank" // 내 자산 순위
    const val MY_ACHIEVEMENT_RATE = "achievement_rate" // 목표달성률

    private lateinit var preferences: SharedPreferences

    // 🚀 초기화 함수 추가 (ApplicationClass에서 초기화할 예정)
    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Cookies 관련 함수 추가 (이전 프로젝트와 동일)
     */
    fun addUserCookie(cookies: HashSet<String>) {
        preferences.edit().putStringSet(COOKIES_KEY_NAME, cookies).apply()
    }

    fun getUserCookie(): MutableSet<String>? {
        return preferences.getStringSet(COOKIES_KEY_NAME, HashSet())
    }

    fun deleteUserCookie() {
        preferences.edit().remove(COOKIES_KEY_NAME).apply()
    }
//    private fun getPreferences(context: Context): SharedPreferences {
//        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//    }

    /**
     * 🚀 값 저장 (오버로딩 적용)
     */
    fun putValue(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun putValue(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun putValue(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun putValue(key: String, value: Float) {
        preferences.edit().putFloat(key, value).apply()
    }

    fun putValue(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    fun putUserId(userId: Long) {
        preferences.edit().putLong(USER_ID, userId).apply()
    }



    /**
     * 🚀 값 가져오기 (오버로딩 적용, 기본값 설정)
     */
    fun getValue(key: String, defaultValue: String = ""): String {
        return preferences.getString(key, defaultValue) ?: defaultValue
    }

    fun getValue(key: String, defaultValue: Int = 0): Int {
        return preferences.getInt(key, defaultValue)
    }

    fun getValue(key: String, defaultValue: Boolean = false): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    fun getValue(key: String, defaultValue: Float = 0f): Float {
        return preferences.getFloat(key, defaultValue)
    }

    fun getValue(key: String, defaultValue: Long = 0L): Long {
        return preferences.getLong(key, defaultValue)
    }

    fun getUserId(): Int {
        return preferences.getInt(USER_ID, 0) // 기본값 0L (ID가 없을 경우)
    }

    /**
     * 특정 값 삭제
     */
    fun removeValue(key: String): Boolean {
        return if (preferences.contains(key)) {
            preferences.edit().remove(key).apply()
            true
        } else {
            false
        }
    }

    /**
     * SharedPreferences 전체 초기화
     */
    fun clearAll() {
        preferences.edit().clear().apply()
    }


}