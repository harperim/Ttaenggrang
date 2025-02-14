package com.ladysparks.ttaenggrang.ui.stock

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.base.BaseTableAdapter
import com.ladysparks.ttaenggrang.databinding.FragmentNewsHistoryTeacherBinding

class NewsHistoryTeacherFragment : BaseFragment<FragmentNewsHistoryTeacherBinding>(
    FragmentNewsHistoryTeacherBinding::bind,
    R.layout.fragment_news_history_teacher
){

    private val viewModel: StockViewModel by viewModels()
    private lateinit var tableAdapter: BaseTableAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //초기화
        initAdapter()

        // LiveData 관찰하여 데이터 변경 시 UI 업데이트
        observeViewModel()


    }


    private fun initAdapter() {

    }

    private fun observeViewModel() {


    }
}
