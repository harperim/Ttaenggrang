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
import com.ladysparks.ttaenggrang.databinding.DialogNewsCreateBinding
import com.ladysparks.ttaenggrang.databinding.DialogNewsDetailBinding
import com.ladysparks.ttaenggrang.databinding.FragmentNewsHistoryStudentBinding
import com.ladysparks.ttaenggrang.databinding.FragmentNewsHistoryTeacherBinding

class NewsHistoryTeacherFragment : BaseFragment<FragmentNewsHistoryTeacherBinding>(
    FragmentNewsHistoryTeacherBinding::bind,
    R.layout.fragment_news_history_teacher
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

        //뉴스생성 버튼
        binding.btnNewsCreate.setOnClickListener {
            createNews()
        }

    }

    // 뉴스 생성 다이얼로그
    private fun createNews() {
        val dialogNewsCreateBinding = DialogNewsCreateBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogNewsCreateBinding.root)

        // 다이얼로그 ui잘리는 현상.
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.6).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // 버튼구현
        dialogNewsCreateBinding.btnCancel.setOnClickListener { // 닫기
            dialog.dismiss()
        }
        dialogNewsCreateBinding.btnReCreate.setOnClickListener {// 뉴스생성
            viewModel.createNews()
            viewModel.newsLiveData.observe(viewLifecycleOwner) { news ->
                news?.let {
                    dialogNewsCreateBinding.textDialogNewsCreateDate.setText(it.createdAt)
                    dialogNewsCreateBinding.textDialogStockName.setText(it.stockName)
                    dialogNewsCreateBinding.textDialogNewsTitle.setText(it.title)
                    dialogNewsCreateBinding.textDialogNewsContent.setText(it.content)

                }
            }
        }
        dialogNewsCreateBinding.btnAdd.setOnClickListener { // fcm 알림+등록

        }

        dialog.show()
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
