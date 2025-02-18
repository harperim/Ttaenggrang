package com.ladysparks.ttaenggrang.ui.stock

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.base.BaseTableAdapter
import com.ladysparks.ttaenggrang.databinding.FragmentStockManageTeacherBinding


class StockManageTeacherFragment : BaseFragment<FragmentStockManageTeacherBinding>(
    FragmentStockManageTeacherBinding::bind,
    R.layout.fragment_stock_manage_teacher
) {

    private val viewModel: StockViewModel by viewModels()
    private lateinit var tableAdapter: BaseTableAdapter
    private val columnWeights = listOf(1f, 1.2f, 1f, 1f, 1.2f, 0.8f, 0.8f)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //초기화
        initAdapter()

        // LiveData 관찰하여 데이터 변경 시 UI 업데이트
        observeViewModel()

        // 서버에서 주식 데이터 가져오기
        viewModel.fetchAllStocks()
        viewModel.fetchStockList()

        //뒤로가기
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }


    private fun initAdapter() {
        tableAdapter = BaseTableAdapter(
            header = listOf(
                "등록일",
                "종목명",
                "주식 종류",
                "카테고리",
                "현재 가격",
                "변동률",
                "겨래 활성도"
            ), // ✅ 헤더 컬럼 설정
            data = emptyList(), // ✅ 초기 데이터 없음
            columnWeights = columnWeights,
            onRowClickListener = { rowIndex, rowData ->
                Toast.makeText(
                    requireContext(),
                    "클릭한 행: ${rowData.joinToString()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        binding.recyclerStockManageList.adapter = tableAdapter
    }

    private fun observeViewModel() {
        viewModel.stockSummaryList.observe(viewLifecycleOwner) { rowData ->
            tableAdapter.updateData(rowData) // ✅ 어댑터에 바로 전달
        }
    }
}
