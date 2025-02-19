package com.ladysparks.ttaenggrang.ui.stock

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration
import com.github.mikephil.charting.charts.LineChart
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.base.BaseTableAdapter
import com.ladysparks.ttaenggrang.base.BaseTableHeaderStickyAdapter
import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.data.model.dto.StockHistoryDto
import com.ladysparks.ttaenggrang.databinding.DialogStockConfirmBinding
import com.ladysparks.ttaenggrang.databinding.DialogStockHistoryDetailBinding
import com.ladysparks.ttaenggrang.databinding.DialogStockTradingBinding
import com.ladysparks.ttaenggrang.databinding.FragmentStockManageStudentBinding
import com.ladysparks.ttaenggrang.ui.component.LineChartComponent
import com.ladysparks.ttaenggrang.util.CustomDateUtil
import com.ladysparks.ttaenggrang.util.NumberUtil
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil

class StockManageStudentFragment : BaseFragment<FragmentStockManageStudentBinding>(
    FragmentStockManageStudentBinding::bind,
    R.layout.fragment_stock_manage_student
) {

    private val viewModel: StockViewModel by viewModels()
    private var studentId: Int = -1
    private lateinit var tableAdapter: BaseTableAdapter
    private var stockHistoryList: List<StockHistoryDto> = emptyList()
    private lateinit var lineChartComponent: LineChartComponent

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //초기화
        initAdapter()

        // LiveData 관찰하여 데이터 변경 시 UI 업데이트
        observeViewModel()

        // 서버에서 데이터 가져오기
        viewModel.fetchAllStocks()
        viewModel.fetchBalance()
        viewModel.fetchStudentStockTransactions()
        viewModel.fetchStockHistory()

        // studentId 가져오기
        studentId = SharedPreferencesUtil.getUserId()

        //뒤로가기
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initAdapter() {
        // 학생 보유 주식 목록 테이블 생성
        tableAdapter = BaseTableAdapter(
            header = listOf(
                "매수일",
                "주식명",
                "유형",
                "보유 주식 수",
                "평균 매입 단가",
                "현재주가",
                "평가금액",
                "수익률",
                "손익금액"
            ), // ✅ 헤더 컬럼 설정
            data = emptyList(), // ✅ 초기 데이터 없음
            onRowClickListener = { rowIndex, rowData ->
                Toast.makeText(
                    requireContext(),
                    "클릭한 행: ${rowData.joinToString()}",
                    Toast.LENGTH_SHORT
                ).show()

                val stockName = rowData[1] // 주식명 가져오기
                val stockId = viewModel.stockList.value?.find { it.name == stockName }?.id

                if (stockId == null) {
                    Log.e("StockManageStudent", "stockId 찾기 실패: $stockName")
                    return@BaseTableAdapter
                }

                // ✅ 해당 주식의 거래 히스토리 필터링
                val filteredHistory = viewModel.stockHistory.value?.filter { it.stockId == stockId }

                if (filteredHistory != null && filteredHistory.isNotEmpty()) {
                    showStockDetailDialog(stockId, stockName) // ✅ 다이얼로그 실행
                } else {
                    Toast.makeText(requireContext(), "거래 히스토리가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.recyclerStudentStockList.adapter = tableAdapter
//        binding.recyclerStudentStockList.apply {
//            adapter = tableAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//
//            // ✅ StickyHeaderDecoration 적용
//            addItemDecoration(StickyHeaderDecoration(tableAdapter))
//
//            // ✅ RecyclerView 패딩 설정 (헤더 높이 반영)
//            setPadding(0, resources.getDimensionPixelSize(R.dimen.sticky_header_height), 0, 0)
//            clipToPadding = true
//
//
//        }

    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun showStockDetailDialog(stockId: Int, stockName: String) {
        viewModel.setSelectedStockInfo(stockId)

        val dialogBinding = DialogStockHistoryDetailBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        // ✅ 다이얼로그 크기 설정
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.55).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // 바인딩
        dialogBinding.textHeadStockName.text = stockName

        // ✅ 차트 컴포넌트 초기화
        lineChartComponent = dialogBinding.chartStock

        viewModel.selectedStockInfo.observe(viewLifecycleOwner) { stockInfo ->
            if (stockInfo != null) {
                dialogBinding.textHeadStockName.text = stockInfo.stockName
                dialogBinding.textHeadStockPrice.text = "${stockInfo.currentPrice}"
                dialogBinding.textHeadStockChange.text = "${stockInfo.changeRate}%"
                dialogBinding.textStockTradingDate2.text = stockInfo.purchaseDate
                dialogBinding.textStockPerPrice2.text = "${stockInfo.avgPurchasePrice}"
                dialogBinding.textStockQuantityTitle2.text = "${stockInfo.ownedShares}주"

                // 차트 설정
                setupChart(dialogBinding.chartStock, stockInfo.stockId, stockInfo.avgPurchasePrice)
            } else {
                Log.e("StockDialog", "🚨 주식 정보를 불러오지 못했습니다. stockId: $stockId")
            }

            // "거래" 버튼 클릭 시 거래 선택 다이얼로그 실행
            dialogBinding.btnTrade.setOnClickListener {
                dialog.dismiss() // 상세 다이얼로그 닫기
                if (stockInfo != null) {
                    showTradingDialog(stockInfo)
                } else {
                    Log.e("StockDialog", "🚨 주식 정보를 불러오지 못했습니다. stockId: $stockId")
                }
            }
        }
        // 닫기 버튼
        dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    // 그래프 세팅
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupChart(chart: LineChart, stockId: Int, avgPurchasePrice: Int) {

        // ✅ ViewModel에서 stockHistory 데이터를 가져와 필터링
        val stockHistoryList = viewModel.stockHistory.value ?: emptyList()
        val filteredStockData = stockHistoryList.filter { history -> history.stockId == stockId }

        if (filteredStockData.isNotEmpty()) {
            Log.d("StockChart", "불러온 주식 데이터 개수: ${filteredStockData.size}")
            Log.d("StockChart", "전체 주식 히스토리 개수: ${stockHistoryList.size}")

            // ✅ 최근 7일치 데이터 가져오기
            val last7DaysStockData = filteredStockData.takeLast(7)

            // ✅ 최근 7일치 데이터 로그 출력
            last7DaysStockData.forEach { data ->
                Log.d("StockChart", "최근 7일 데이터: ${data.date}, ${data.price}")
            }

            // ✅ 날짜 변환 (YYYY-MM-DD → MM-DD)
            val dateLabels = last7DaysStockData.map {
                CustomDateUtil.formatToDate(it.date)
            }

            // ✅ MPAndroidChart Entry 리스트 생성
            val stockHistory = last7DaysStockData.mapIndexed { index, data ->
                Pair(index.toFloat(), data.price)
            }
            Log.d("StockChart", "차트에 적용할 평균 매입 단가: $avgPurchasePrice")

            // ✅ 차트 적용
            lineChartComponent.setChartData(
                stockHistory,
                dateLabels,
                R.color.chartBlue,
                avgPurchasePrice
            )
        } else {
            Log.e("StockChart", "선택한 주식(${stockId})의 주식 데이터 없음")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showTradingDialog(stockInfo: StockDetailInfo) {
        val dialogBinding = DialogStockTradingBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        // ✅ 다이얼로그 UI 크기 조정
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.4).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // ✅ 주식명 & 현재 주가 설정
        dialogBinding.textDialogStockTitle.setText(stockInfo.stockName.substringBefore(" "))
        dialogBinding.textDialogStockPrice.setText(stockInfo.currentPrice.toString())

        // ✅ 보유 주식 수 설정 (StockDetailInfo 활용)
        dialogBinding.textDialogMyStock.setText(stockInfo.ownedShares.toString())

        // ✅ 사용자가 거래할 주식 수 입력 시, 즉시 계산값 변경
        dialogBinding.textDialogStockTrade.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val inputAmount = s.toString().toIntOrNull() ?: 0
                val expectedPayment = stockInfo.currentPrice * inputAmount // ✅ 현재 주가 * 입력한 주식 수
                dialogBinding.textContent1.text = "$expectedPayment"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // ✅ 매도 버튼 클릭 시
        dialogBinding.btnSell.setOnClickListener {
            val sellCount = dialogBinding.textDialogStockTrade.text.toString().toIntOrNull() ?: 0

            when {
                sellCount <= 0 -> {
                    Toast.makeText(requireContext(), "올바른 수량을 입력하세요.", Toast.LENGTH_SHORT).show()
                }

                sellCount > stockInfo.ownedShares -> { // ✅ 보유 주식보다 많은 경우 예외 처리
                    Toast.makeText(
                        requireContext(),
                        "보유한 주식보다 많은 수량을 매도할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    viewModel.updateTradeAmount(
                        sellCount,
                        stockInfo.currentPrice,
                        stockInfo.ownedShares,
                        "SELL"
                    )
                    dialog.dismiss()
                    showConfirmDialog(stockInfo, sellCount, "SELL")
                }
            }
        }

        // ✅ 매수 버튼 클릭 시
        dialogBinding.btnBuy.setOnClickListener {
            val buyCount = dialogBinding.textDialogStockTrade.text.toString().toIntOrNull() ?: 0
            val totalCost = stockInfo.currentPrice * buyCount
            val balance = viewModel.balance.value ?: 0

            when {
                buyCount <= 0 -> {
                    Toast.makeText(requireContext(), "올바른 수량을 입력하세요.", Toast.LENGTH_SHORT).show()
                }

                totalCost > balance -> {
                    Toast.makeText(requireContext(), "보유 현금이 부족합니다.", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    viewModel.updateTradeAmount(
                        buyCount,
                        stockInfo.currentPrice,
                        stockInfo.ownedShares,
                        "BUY"
                    )
                    dialog.dismiss()
                    showConfirmDialog(stockInfo, buyCount, "BUY")
                }
            }
        }
        dialog.show()
    }


    // 매도, 매수 거래 다이얼로그
    private fun showConfirmDialog(
        stockInfo: StockDetailInfo,
        tradeAmount: Int,
        transactionType: String
    ) {
        val confirmDialogBinding = DialogStockConfirmBinding.inflate(layoutInflater)
        val confirmDialog = Dialog(requireContext())
        confirmDialog.setContentView(confirmDialogBinding.root)

        // 다이얼로그 크기 조정
        confirmDialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.4).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // 다이얼로그 타이틀 설정 (매도 or 매수)
        confirmDialogBinding.textDialogTitle.text =
            if (transactionType == "SELL") "매도 하시겠습니까?" else "매수 하시겠습니까?"

        // 예상 결제 금액
        viewModel.expectedPayment.observe(confirmDialogBinding.root.context as LifecycleOwner) { amount ->
            confirmDialogBinding.textContent1.text = "$amount" // ✅ 예상 결제 금액 표시
        }

        // 거래 후 보유 현금 표시
        viewModel.updatedBalance.observe(confirmDialogBinding.root.context as LifecycleOwner) { updatedAsset ->
            confirmDialogBinding.textContent2.text = "$updatedAsset"
        }

        // ✅ 거래 후 보유 주식 업데이트
        viewModel.updatedOwnedStock.observe(confirmDialogBinding.root.context as LifecycleOwner) { newStock ->
            if (transactionType == "SELL") {
                confirmDialogBinding.textContent3.text = "${newStock} 주"

            } else {
                confirmDialogBinding.textContent3.text = "${newStock} 주"
            }
        }

        // ✅ "거래하기" 버튼 클릭 시, 매도 또는 매수 실행
        confirmDialogBinding.btnYes.setOnClickListener {
            if (transactionType == "SELL") {
                viewModel.sellStock(stockInfo.stockId, tradeAmount, studentId)
            } else {
                viewModel.buyStock(stockInfo.stockId, tradeAmount, studentId)
            }
            confirmDialog.dismiss()
        }

        // ✅ "취소" 버튼 클릭 시 다이얼로그 닫기
        confirmDialogBinding.btnNo.setOnClickListener {
            confirmDialog.dismiss()
        }
        confirmDialog.show()
    }



    private fun observeViewModel() {
        viewModel.stockTransactionHistory.observe(viewLifecycleOwner) { transactions ->
            Log.d("StockFragment", "stockTransaction 업데이트됨: $transactions")
            if (transactions != null && transactions.isNotEmpty()) {
                viewModel.updateStockTableData() // ✅ 거래 내역이 있으면 업데이트
            }
        }

        viewModel.stockTableData.observe(viewLifecycleOwner) { newData ->
            Log.d("StockFragment", "stockTableData 업데이트됨: $newData")
            tableAdapter.updateData(newData)
        }

        viewModel.balance.observe(viewLifecycleOwner) { balance ->
            binding.textContent5.text = NumberUtil.formatWithComma("$balance")
        }

        viewModel.stockSummary.observe(viewLifecycleOwner) { summary ->
            binding.textContent1.text = NumberUtil.formatWithComma((summary["totalInvestment"] as? Number)?.toLong() ?: 0L)
            binding.textContent2.text = NumberUtil.formatWithComma((summary["totalValuation"] as? Number)?.toLong() ?: 0L)

        }

        viewModel.totalProfit.observe(viewLifecycleOwner) { totalProfit ->
            binding.textContent3.text = NumberUtil.formatWithComma(totalProfit)
        }

        viewModel.totalYield.observe(viewLifecycleOwner) { totalYield ->
            binding.textContent4.text = "%.2f%%".format(totalYield)
        }

        viewModel.selectedStockInfo.observe(viewLifecycleOwner) { stockInfo ->
            if (stockInfo != null) {
                Log.d("StockFragment", "✅ 주식 상세 정보 업데이트: $stockInfo")
            } else {
                Log.e("StockFragment", "🚨 주식 상세 정보를 가져오지 못했습니다.")
            }
        }
    }
}
