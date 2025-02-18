package com.ladysparks.ttaenggrang.ui.stock

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.base.BaseTableAdapter
import com.ladysparks.ttaenggrang.databinding.FragmentBankManageStudentBinding
import com.ladysparks.ttaenggrang.ui.home.BankViewModel


class BankManageStudentFragment : BaseFragment<FragmentBankManageStudentBinding>(
    FragmentBankManageStudentBinding::bind,
    R.layout.fragment_bank_manage_student
) {

    private val viewModel: BankViewModel by viewModels()
    private lateinit var tableAdapter: BaseTableAdapter
    private val columnWeights = listOf(0.5f, 1f, 0.7f, 0.7f, 0.7f, 1f, 1.2f,1.2f,1.2f)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //초기화
        initAdapter()

        // LiveData 관찰하여 데이터 변경 시 UI 업데이트
        observeViewModel()

        // 서버에서 데이터 가져오기

    }

    private fun initAdapter() {
        tableAdapter = BaseTableAdapter(
            header = listOf(
                "No.",
                "상품명",
                "상품 유형",
                "가입 기간",
                "이자율",
                "납입금액",
                "가입일",
                "만기일",
                "예상 지급액"
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
        binding.recyclerBankManageList.adapter = tableAdapter
    }

    private fun observeViewModel() {

    }
}
