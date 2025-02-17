package com.ladysparks.ttaenggrang.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Date, String, Long, Calendar 형식의 데이터를 원하는 포맷(yyyy.MM.dd) 에 맞게 변환
 * - 3자리 마다 ',' 를 찍어 한국식 화폐 단위로 구분 하는 유틸
 *
 * 사용 방법 : 함수 호출 후, 괄호 안에 변환할 타입의 데이터를 넣어줍니다.
 * 함수는 크게 2가지로 구분 됩니다. 반환값이 TimeStamp 인 경우와, StringFormat 인 경우
 *
 * - 1. 반환값으로 TimeStamp 를 받고싶은 경우 (주로 서버에 Date 타입의 정보를 저장할 때 사용)
 * DateUtil.formatToTimestamp(...)
 *
 * - 2. 반환값으로 StringFormat  을 받고 싶은 경우 (주로 UI 에 표현될 때 사용)
 * DataUtil.formatDate(...)
 */
object DataUtil {

    private const val DATE_FORMAT = "yyyy.MM.dd"
    private const val DATE_TIME_FORMAT = "yyyy.MM.dd HH:mm:ss"

    /***************  서버의 DATETIME 형식을 String 으로 바꾸기 위함
     * 1 단계 : 서버에서 받은 데이터(String) 값을 데이터 타입으로 변환
     *      - val parsedDate = DataUtil.formatDateTimeFromServer(serverDate)
     * 2 단계 : 변환된 데이터 타입을 원하는 형식으로 변환
     *      - val formattedDate = DataUtil.formatDate(parsedDate)
     * **********/
    // 서버에서 오는 DATETIME을 Date로 변환
    fun formatDateTimeFromServer(dateString: String): Date? {
        val possibleFormats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss",       // 2024-02-14T10:30:00
            "yyyy-MM-dd HH:mm:ss",         // 2024-02-14 10:30:00
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" // 2024-02-14T10:30:00.123Z
        )

        for (format in possibleFormats) {
            try {
                val formatter = SimpleDateFormat(format, Locale.getDefault())
                return formatter.parse(dateString)
            } catch (e: Exception) {
                // 실패하면 다음 포맷으로 시도
            }
        }
        return null // 모든 포맷에서 실패하면 null 반환
    }
    // Date → String 변환 (UI 출력용)
    fun formatDate(date: Date, format: String = DATE_FORMAT): String {
        val formatter = SimpleDateFormat(format, Locale.getDefault())
        return formatter.format(date)
    }

    // 서버 DATETIME을 String(yyyy.MM.dd)로 변환하는 함수
    fun formatDateTimeToDisplay(dateString: String): String {
        val date = formatDateTimeFromServer(dateString)
        return date?.let { formatDate(it) } ?: "Invalid Date"
    }


    fun convertDateTime(value: Any, forDisplay: Boolean = false): Any? {
        return when (value) {
            is String -> { // String → Date 변환 (서버에서 받은 데이터 처리)
                val possibleFormats = listOf(
                    "yyyy-MM-dd'T'HH:mm:ss",        // 2024-02-14T10:30:00
                    "yyyy-MM-dd HH:mm:ss",          // 2024-02-14 10:30:00
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", // 2024-02-14T10:30:00.123Z
                    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",  // 2024-02-14T10:30:00.233456 (DATETIME(6) 지원 추가)
                    "yyyy.MM.dd"
                )

                for (format in possibleFormats) {
                    try {
                        val formatter = SimpleDateFormat(format, Locale.getDefault())
                        return formatter.parse(value)
                    } catch (e: Exception) {
                        // 실패하면 다음 포맷으로 시도
                    }
                }
                null // 모든 포맷에서 실패하면 null 반환
            }

            is Date -> { // Date → String 변환 (서버로 보낼 데이터 or UI 표시용)
                val format = if (forDisplay) "yyyy.MM.dd" else "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
                val formatter = SimpleDateFormat(format, Locale.getDefault())
                formatter.format(value)
            }

            else -> null // 지원하지 않는 타입
        }
    }



    /***************  기타 **********/
    // String (yyyy.MM.dd) → Long (Timestamp) 변환
    fun formatToTimestamp(dateString: String): Long? {
        return try {
            val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val date = formatter.parse(dateString)
            date?.time // Date를 Timestamp(Long)으로 변환
        } catch (e: Exception) {
            null
        }
    }

    //  Date → Long (Timestamp) 변환
    fun formatToTimestamp(date: Date): Long {
        return date.time
    }

    // Calendar → Long (Timestamp) 변환
    fun formatToTimestamp(calendar: Calendar): Long {
        return calendar.timeInMillis
    }


    // String to Date
    fun formatDate(dateString: String): Date? {
        return try {
            val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            formatter.parse(dateString)
        } catch (e: Exception) {
            null  // 변환 실패 시 null 반환
        }
    }

    // Long(Timestamp) to String : 주로 서버에서 받은 DateFormat 을 변환할 때 사용될 예정
    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        return formatDate(date)
    }

    // Date to String
    fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return formatter.format(date)
    }

    // Calendar to String
    fun formatDate(calendar: Calendar): String {
        return formatDate(calendar.time)
    }

}