package com.ladysparks.ttaenggrang.ui.stock

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.base.BaseTableAdapter
import com.ladysparks.ttaenggrang.databinding.FragmentNewsHistoryStudentBinding

class NewsHistoryStudentFragment : BaseFragment<FragmentNewsHistoryStudentBinding>(
    FragmentNewsHistoryStudentBinding::bind,
    R.layout.fragment_news_history_student
){

    private val viewModel: StockViewModel by viewModels()
    private lateinit var tableAdapter: BaseTableAdapter
    private val columnWeights = listOf(0.5f, 1f, 2f, 0.8f, 0.8f)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //초기화
        initAdapter()

        // LiveData 관찰하여 데이터 변경 시 UI 업데이트
        observeViewModel()

    }


    private fun initAdapter() {
        println("✅ NewsHistoryStudentFragment - columnWeights: $columnWeights")
        tableAdapter = BaseTableAdapter(
            header = listOf(
                "No.",
                "등록일",
                "제목",
                "관련 주식",
                "호재/악재"
            ),
            data = emptyList(),
            columnWeights = columnWeights,
            onRowClickListener = { rowIndex, rowData ->
                Toast.makeText(
                    requireContext(),
                    "클릭한 행: ${rowData.joinToString ()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        binding.recyclerNewsHistoryTable.adapter = tableAdapter

    }

    private fun observeViewModel() {


    }
}
