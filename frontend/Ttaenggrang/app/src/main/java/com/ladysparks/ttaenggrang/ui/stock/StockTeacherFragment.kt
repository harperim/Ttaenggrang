package com.ladysparks.ttaenggrang.ui.stock

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
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

//        // 스위치 설정
//        setupSwitchListener()
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
        viewModel.stockList.observe(viewLifecycleOwner, Observer { stockList ->
            stockList?.takeIf { it.isNotEmpty() }?.let {
                stockAdapter.updateData(it)  // 어댑터 데이터 업데이트
            }
        })
    }

    // 아이템 클릭 이벤트 처리
    override fun onStockClick(stock: StockDto) {
        Toast.makeText(requireContext(), "선택한 주식: ${stock.name}", Toast.LENGTH_SHORT).show()
        binding.textHeadStockName.text = stock.name.substringBefore(" ")
        binding.textHeadStockPrice.text = stock.pricePer.toString()
        binding.textHeadStockChange.text = "${stock.changeRate}%"
    }




//    private fun setupSwitchListener() {
//        binding.btnStockOpen.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                // 스위치가 ON일 때 track, thumb이 selector 파일의 ON 상태를 따라감
//                binding.btnStockOpen.trackTintList = requireContext().getColorStateList(R.color.foundation_green_500)
//                binding.btnStockOpen.thumbTintList = requireContext().getColorStateList(R.color.white)
//            } else {
//                // 스위치가 OFF일 때 track, thumb이 selector 파일의 OFF 상태를 따라감
//                binding.btnStockOpen.trackTintList = requireContext().getColorStateList(R.color.backgroundGray)
//                binding.btnStockOpen.thumbTintList = requireContext().getColorStateList(R.color.black200)
//            }
//        }
//    }
}
