package com.ladysparks.ttaenggrang.ui.stock

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.base.BaseTableAdapter
import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.databinding.FragmentStockListStudentBinding
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil

class StockListStudentFragment : BaseFragment<FragmentStockListStudentBinding>(
    FragmentStockListStudentBinding::bind,
    R.layout.fragment_stock_list_student
), OnStockClickListener {

    private val viewModel: StockViewModel by viewModels()
    private var studentId: Int = -1
    private lateinit var tableAdapter: BaseTableAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //초기화
        initAdapter()

        // LiveData 관찰하여 데이터 변경 시 UI 업데이트
        observeViewModel()

        // 서버에서 주식 데이터 가져오기
        viewModel.fetchAllStocks()

        // 서버에서 거래 가능 현금 가져오기
        viewModel.fetchBalance()

        // studentId 가져오기 (예: SharedPreferences에서 가져오기)
        studentId = SharedPreferencesUtil.getUserId()

        // 서버에서 학생 주식 보유 내역 가져오기
        viewModel.fetchOwnedStocks(studentId)


    }

    override fun onStockClick(stock: StockDto) {

    }

    private fun initAdapter() {
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
            }
        )
        binding.recyclerStudentStockList.adapter = tableAdapter
    }

    private fun observeViewModel() {
        // ✅ ViewModel에서 주식 데이터 가져와서 RecyclerView 업데이트
        viewModel.ownedStocks.observe(viewLifecycleOwner) { ownedStocks ->
            val newData = ownedStocks.map { stock ->
                val ValuationAmount = stock.ownedQty * stock.currentPrice //평가금액
                //val totalProfit = marketValue - stock.
                BaseTableRowModel(
                    listOf(
                        stock.purchaseDate,
                        stock.stockName,
                        "유형",
                        stock.ownedQty.toString(), //보유 주식 수
                        "평균매입단가",
                        stock.currentPrice.toString(), //현재주가
                        ValuationAmount.toString(), //평가금액
                        "수익률",
                        "손익금액"
                    )
                )
            }
            tableAdapter.updateData(newData) // ✅ 데이터 업데이트
            val totalValuationAmount = ownedStocks.sumOf { it.ownedQty * it.currentPrice }
            binding.textContent2.text = "${totalValuationAmount}"
        }

        viewModel.balance.observe(viewLifecycleOwner){ balance ->
            binding.textContent5.text = "$balance"
        }

    }
}
