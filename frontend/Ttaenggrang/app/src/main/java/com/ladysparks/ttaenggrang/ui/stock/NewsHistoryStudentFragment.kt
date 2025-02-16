package com.ladysparks.ttaenggrang.ui.stock

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.base.BaseTableAdapter
import com.ladysparks.ttaenggrang.base.BaseTableStyleAdapter
import com.ladysparks.ttaenggrang.data.model.dto.NewsDto
import com.ladysparks.ttaenggrang.databinding.DialogNewsDetailBinding
import com.ladysparks.ttaenggrang.databinding.FragmentNewsHistoryStudentBinding
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel
import com.ladysparks.ttaenggrang.util.DataUtil

class NewsHistoryStudentFragment : BaseFragment<FragmentNewsHistoryStudentBinding>(
    FragmentNewsHistoryStudentBinding::bind,
    R.layout.fragment_news_history_student
){

    private val viewModel: StockViewModel by viewModels()
    private lateinit var tableAdapter: BaseTableStyleAdapter
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
        tableAdapter = BaseTableStyleAdapter(
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
                val selectedNewsId = viewModel.newsListLiveData.value?.getOrNull(rowIndex)?.id
                selectedNewsId?.let { newsId ->
                    viewModel.fetchNewsDetail(newsId)
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

        // 뉴스 상세 정보 다이얼로그 표시
        viewModel.newsDetailLiveData.observe(viewLifecycleOwner) { newsDetail ->
            newsDetail?.let { showNewsDetailDialog(it) } // 다이얼로그 표시
        }
    }

    private fun showNewsDetailDialog(news: NewsDto) {
        val dialogBinding = DialogNewsDetailBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.5).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // 뉴스 데이터 바인딩
        dialogBinding.textDialogTitle.text = "땡그랑뉴스"
        dialogBinding.textDialogContent.text = DataUtil.formatDateTimeToDisplay(news.createdAt)
        dialogBinding.textNewsTitle.text = "\"${news.title}\""
        dialogBinding.textNewsContent.text = news.content

        // 닫기
        dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    // 🔹 NewsDto를 테이블 행 데이터로 변환하는 확장 함수
    private fun NewsDto.toTableRow(index: Int): BaseTableRowModel {
        return BaseTableRowModel(
            listOf(
                index.toString(),       // 뉴스 번호 (No.)
                DataUtil.formatDateTimeToDisplay(createdAt),              // 등록일
                title,                  // 제목
                stockName,              // 관련 주식
                if (newsType == "POSITIVE") "호재" else "악재" // ✅ 호재/악재 변환
            )
        )
    }
}
