package com.ladysparks.ttaenggrang.ui.stock

import android.os.Bundle
import android.util.Log
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

        // studentId 가져오기
        studentId = SharedPreferencesUtil.getUserId()

        // 서버에서 학생 주식 보유 내역 가져오기
        viewModel.fetchOwnedStocks(studentId)
        viewModel.fetchStudentStockTransactions(studentId)

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
        viewModel.ownedStocks.observe(viewLifecycleOwner) { ownedStocks ->
            Log.d("StockFragment", "📌 ownedStocks 업데이트됨: $ownedStocks")
            if (ownedStocks.isNotEmpty() && viewModel.stockTransaction.value != null) {
                viewModel.updateStockTableData(studentId)
            }
        }

        viewModel.stockTransaction.observe(viewLifecycleOwner) { transactions ->
            Log.d("StockFragment", "📌 stockTransaction 업데이트됨: $transactions")
            if (transactions.isNotEmpty() && viewModel.ownedStocks.value != null) {
                viewModel.updateStockTableData(studentId)
            }
        }

        viewModel.stockTableData.observe(viewLifecycleOwner) { newData ->
            Log.d("StockFragment", "📌 stockTableData 업데이트됨: $newData")
            tableAdapter.updateData(newData)
        }

        viewModel.balance.observe(viewLifecycleOwner) { balance ->
            binding.textContent5.text = "$balance"
        }

        viewModel.stockSummary.observe(viewLifecycleOwner) { summary ->
            binding.textContent1.text = "${summary["totalInvestment"]} 원"
            binding.textContent2.text = "${summary["totalValuation"]} 원"
            binding.textContent3.text = "${summary["totalProfit"]} 원"
            binding.textContent4.text = "%.2f%%".format(summary["totalReturnRate"])
        }

    }
}
