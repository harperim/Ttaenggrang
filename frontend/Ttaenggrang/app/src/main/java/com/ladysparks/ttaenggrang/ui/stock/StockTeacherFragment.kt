package com.ladysparks.ttaenggrang.ui.stock

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.databinding.FragmentStockTeacherBinding
import java.util.Calendar

class StockTeacherFragment : BaseFragment<FragmentStockTeacherBinding>(
    FragmentStockTeacherBinding::bind,
    R.layout.fragment_stock_teacher
), OnStockClickListener {

    private val viewModel: StockViewModel by viewModels()
    private lateinit var stockAdapter: StockAdapter
    private var selectedStock: StockDto? = null // 선택한 주식 저장


    private var startTime: String = ""
    private var endTime: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //초기화
        initAdapter()

        // LiveData 관찰하여 데이터 변경 시 UI 업데이트
        observeViewModel()

        // 서버에서 주식 데이터 가져오기
        viewModel.fetchAllStocks()

        // 시작 시간 선택
        binding.startTime.setOnClickListener {
            showTimePickerDialog(isStartTime = true)
        }

        // 종료 시간 선택
        binding.endTime.setOnClickListener {
            showTimePickerDialog(isStartTime = false)
        }

        //주식장 오픈
        binding.btnStockOpen.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateMarketStatus(isChecked) // 서버로 true/false 전송
            Log.d("TAG", "onViewCreated: switch 클릭!!!!")
        }

        // 지난 뉴스 보기
        binding.btnNewsHistory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NewsHistoryTeacherFragment())
                .addToBackStack(null)
                .commit()
        }

        initData()
    }

    private fun initData() {
        // 화면이 변경될 때 표시할 데이터/API 를 해당 함수에서 호출합니다.
    }

    // TimePickerDialog를 띄우는 함수
    private fun showTimePickerDialog(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)

                if (isStartTime) {
                    startTime = selectedTime
                    binding.startTime.setText("시작 시간: $startTime")
                } else {
                    endTime = selectedTime
                    binding.endTime.setText("종료 시간: $endTime")
                }
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    private fun initAdapter() {
        stockAdapter = StockAdapter(arrayListOf(), this)
        binding.recyclerStockList.adapter = stockAdapter
    }

    private fun observeViewModel() {
        // 어댑터 데이터 업데이트
        viewModel.stockList.observe(viewLifecycleOwner, Observer { stockList ->
            stockList?.takeIf { it.isNotEmpty() }?.let {
                stockAdapter.updateData(it)
            }
        })

        // ui업데이트
        viewModel.selectedStock.observe(viewLifecycleOwner) { stock ->
            stock?.let {
                binding.textHeadStockName.text = it.name.substringBefore(" ")
                binding.textHeadStockPrice.text = it.pricePer.toString()
                binding.textHeadStockChange.text = "${it.changeRate}%"
            }
        }
    }

    // 아이템 클릭 이벤트 처리
    override fun onStockClick(stock: StockDto) {
        Toast.makeText(requireContext(), "선택한 주식: ${stock.name}", Toast.LENGTH_SHORT).show()
        binding.textHeadStockName.text = stock.name.substringBefore(" ")
        binding.textHeadStockPrice.text = stock.pricePer.toString()
        binding.textHeadStockChange.text = "${stock.changeRate}%"
    }

}
