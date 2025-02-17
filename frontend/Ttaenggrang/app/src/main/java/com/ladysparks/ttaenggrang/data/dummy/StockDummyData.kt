package com.ladysparks.ttaenggrang.data.dummy

import android.os.Build
import androidx.annotation.RequiresApi
import com.ladysparks.ttaenggrang.data.model.dto.StockHistoryDto
import java.time.LocalDate
import java.util.Random


//object StockDummyData {
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun generateStockSampleData(stockName: String, stockId: Int): List<StockHistoryDto> {
//        val sampleData = mutableListOf<StockHistoryDto>()
//        val random = Random()
//        var price = 1000f  // 초기 주식 가격
//
//        for (i in 1..30) {
//            val date = LocalDate.now().minusDays(30 - i.toLong()).toString() // 30일 전부터 오늘까지 날짜 생성
//            price += random.nextInt(200) - 100  // 랜덤 변동 (-100 ~ +100)
//            sampleData.add(StockHistoryDto(stockId, stockName, price, date))
//        }
//
//        return sampleData
//    }
//}