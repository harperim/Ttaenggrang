package com.ladysparks.ttaenggrang.ui.stock

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.base.BaseTableAdapter
import com.ladysparks.ttaenggrang.data.model.dto.NewsDto
import com.ladysparks.ttaenggrang.databinding.FragmentNewsHistoryStudentBinding
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel

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

        // 서버에서 데이터 가져오기
        viewModel.fetchNewsList()
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
                val selectedNewsId = rowData.firstOrNull()?.toIntOrNull() // 첫 번째 컬럼에 뉴스 ID 저장
                selectedNewsId?.let { newsId ->
                    viewModel.fetchNewsDetail(newsId) // ✅ 뉴스 상세 정보 요청
                }
            }
        )
        binding.recyclerNewsHistoryTable.adapter = tableAdapter

    }

    private fun observeViewModel() {
        viewModel.newsListLiveData.observe(viewLifecycleOwner) { newsList ->
            newsList?.takeIf { it.isNotEmpty() }?.let { list ->
                val tableData = list.mapIndexed { index, news -> news.toTableRow(index + 1) }
                tableAdapter.updateData(tableData) // 테이블 업데이트
            }
        }

        // 뉴스 상세 정보 관찰
        viewModel.newsDetailLiveData.observe(viewLifecycleOwner) { newsDetail ->
            newsDetail?.let { showNewsDetailDialog(it) } // 다이얼로그 표시
        }

    }

    private fun showNewsDetailDialog(it: NewsDto) {

    }

    // 🔹 NewsDto를 테이블 행 데이터로 변환하는 확장 함수
    private fun NewsDto.toTableRow(index: Int): BaseTableRowModel {
        return BaseTableRowModel(
            listOf(
                index.toString(),       // ✅ 뉴스 번호 (No.)
                createdAt,              // ✅ 등록일
                title,                  // ✅ 제목
                stockName,              // ✅ 관련 주식
                if (newsType == "POSITIVE") "호재" else "악재" // ✅ 호재/악재 변환
            )
        )
    }
}
