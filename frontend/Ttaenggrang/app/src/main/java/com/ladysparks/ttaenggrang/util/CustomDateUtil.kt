package com.ladysparks.ttaenggrang.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * DateUtil - 서버로부터 받은 날짜 데이터를 파싱하여 원하는 형식으로 변환
 *
 * - 서버에서 받을 수 있는 날짜 형식:
 *   1. "yyyy-MM-dd'T'HH:mm:ss"  →  2024-02-14T10:30:00
 *   2. "yyyy-MM-dd HH:mm:ss"    →  2024-02-14 10:30:00
 *   3. "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" →  2024-02-14T10:30:00.123Z
 *
 * - 변환할 형식:
 *   1. "yyyy.MM.dd" →  2024.02.14
 *   2. "yyyy.MM.dd HH:mm:ss" →  2024.02.14 10:30:00
 */
object CustomDateUtil {

    private const val DATE_MONTH_DAY = "MM-dd"
    private const val DATE_FORMAT = "yyyy.MM.dd"
    private const val DATE_TIME_FORMAT = "yyyy.MM.dd HH:mm:ss"

    // 서버에서 받을 수 있는 여러 날짜 포맷
    private val serverDateFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    )

    /**
     * 서버에서 받은 날짜(String, Date-time) 값을 Date 객체로 변환
     * fun format... 함수 내에서 사용되는 것이므로, 사용자가 변환할때 쓰지는 않음.
     */
    fun parseServerDate(dateString: String): Date? {
        for (format in serverDateFormats) {
            try {
                return SimpleDateFormat(format, Locale.getDefault()).parse(dateString)
            } catch (_: Exception) { }
        }
        return null // 변환 실패 시 null 반환
    }

    fun formatToMonthDay(dateString: String): String{
        return parseServerDate(dateString)?.let {
            SimpleDateFormat(DATE_MONTH_DAY, Locale.getDefault()).format(it)
        } ?: "Invalid Date"
    }

    /**
     * 서버에서 받은 날짜를 "yyyy.MM.dd" 형식으로 변환 (UI에 표시할 때 사용)
     */
    fun formatToDate(dateString: String): String {
        return parseServerDate(dateString)?.let {
            SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(it)
        } ?: "Invalid Date"
    }

    /**
     * 서버에서 받은 날짜를 "yyyy.MM.dd HH:mm:ss" 형식으로 변환 (UI에 표시할 때 사용)
     */
    fun formatToDateTime(dateString: String): String {
        return parseServerDate(dateString)?.let {
            SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(it)
        } ?: "Invalid DateTime"
    }
}
