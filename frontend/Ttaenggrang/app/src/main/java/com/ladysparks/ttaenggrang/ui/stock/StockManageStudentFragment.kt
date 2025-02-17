package com.ladysparks.ttaenggrang.ui.stock

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.base.BaseTableAdapter
import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.data.model.dto.StockHistoryDto
import com.ladysparks.ttaenggrang.databinding.DialogStockDetailBinding
import com.ladysparks.ttaenggrang.databinding.DialogStockHistoryDetailBinding
import com.ladysparks.ttaenggrang.databinding.FragmentStockManageStudentBinding
import com.ladysparks.ttaenggrang.ui.component.LineChartComponent
import com.ladysparks.ttaenggrang.util.CustomDateUtil
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import java.time.format.DateTimeFormatter

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

        // studentId 가져오기
        studentId = SharedPreferencesUtil.getUserId()
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
//                Toast.makeText(
//                    requireContext(),
//                    "클릭한 행: ${rowData.joinToString()}",
//                    Toast.LENGTH_SHORT
//                ).show()

                val stockId = rowData[1].toIntOrNull() ?: return@BaseTableAdapter // ✅ 주식 ID 가져오기
                val stockName = rowData[1]

                // ✅ 해당 주식의 거래 히스토리 필터링
                val filteredHistory = stockHistoryList.filter { it.stockId == stockId }

                if (filteredHistory.isNotEmpty()) {
                    showStockDetailDialog(stockId, stockName, filteredHistory) // ✅ 다이얼로그 실행
                } else {
                    Toast.makeText(requireContext(), "거래 히스토리가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.recyclerStudentStockList.adapter = tableAdapter

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showStockDetailDialog(stockId: Int, stockName: String, stockHistory: List<StockHistoryDto>) {
        val dialogBinding = DialogStockHistoryDetailBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        // ✅ 다이얼로그 크기 설정
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // ✅ 주식명 표시
        dialogBinding.textHeadStockName.text = stockName

        // ✅ MPAndroidChart 설정
        setupChart(dialogBinding.chartStock, stockId)

        // ✅ 닫기 버튼
        dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupChart(chart: LineChart, stockId: Int) {


        // ✅ ViewModel에서 선택된 주식의 히스토리 데이터 가져오기
        viewModel.stockHistory.observe(viewLifecycleOwner) { stockHistoryList ->
            val filteredStockData = stockHistoryList.filter { history -> history.stockId == stockId }

            if (filteredStockData.isNotEmpty()) {
                Log.d("StockChart", "불러온 주식 데이터 개수: ${filteredStockData.size}")
                Log.d("StockChart", "전체 주식 히스토리 개수: ${stockHistoryList.size}")

                // 최근 7일치 데이터만 가져오기
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

                // ✅ 차트 적용
                lineChartComponent.setChartData(stockHistory, dateLabels, R.color.chartBlue)
            } else {
                Log.d("StockChart", "선택한 주식(${stockId})의 주식 데이터 없음")
            }
        }
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
            binding.textContent5.text = "$balance"
        }

        viewModel.stockSummary.observe(viewLifecycleOwner) { summary ->
            binding.textContent1.text = "${summary["totalInvestment"]}"
            binding.textContent2.text = "${summary["totalValuation"]}"
        }

        viewModel.totalProfit.observe(viewLifecycleOwner) { totalProfit ->
            binding.textContent3.text = "${totalProfit}"
        }

        viewModel.totalYield.observe(viewLifecycleOwner) { totalYield ->
            binding.textContent4.text = "%.2f%%".format(totalYield)
        }

    }
}
