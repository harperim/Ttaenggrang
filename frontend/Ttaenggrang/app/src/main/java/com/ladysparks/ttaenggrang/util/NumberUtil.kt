package com.ladysparks.ttaenggrang.util

import java.text.DecimalFormat


/**
 * int, double, String 타입의 숫자
 * - 3자리 마다 ',' 를 찍어 한국식 화폐 단위로 구분 하는 유틸
 *
 * [사용 방법]
 * - NumberUtil.formatWithComma(1000000)
 */
object NumberUtil {

    fun formatWithComma(value: Int): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(value)
    }

    fun formatWithComma(value: Double): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(value)
    }

    fun formatWithComma(value: String): String {
        return try {
            val number = value.replace(",", "").toDouble()
            formatWithComma(number)
        } catch (e: NumberFormatException) {
            value // 숫자가 아닐 경우 원본 그대로 반환
        }
    }



}