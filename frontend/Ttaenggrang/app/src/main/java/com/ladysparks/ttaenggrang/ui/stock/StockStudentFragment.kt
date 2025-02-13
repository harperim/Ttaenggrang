package com.ladysparks.ttaenggrang.ui.stock

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.data.model.request.StudentSignInRequest
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.DialogStockTradingBinding
import com.ladysparks.ttaenggrang.databinding.FragmentStockStudentBinding
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StockStudentFragment : BaseFragment<FragmentStockStudentBinding>(
    FragmentStockStudentBinding::bind,
    R.layout.fragment_stock_student
), OnStockClickListener {

    private val viewModel: StockViewModel by viewModels()
    private lateinit var stockAdapter: StockAdapter
    private var selectedStock: StockDto? = null // 선택한 주식 저장

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //초기화
        initAdapter()

        // LiveData 관찰하여 데이터 변경 시 UI 업데이트
        observeViewModel()

        // 서버에서 주식 데이터 가져오기
        viewModel.fetchAllStocks()

        //거래 버튼
        binding.btnTrade.setOnClickListener {
            val selectedStock = viewModel.selectedStock.value
            selectedStock?.let { stock ->
                showDialog(stock)
            } ?: Toast.makeText(requireContext(), "먼저 주식을 선택해주세요.", Toast.LENGTH_SHORT).show()
        }



    }

    private fun showDialog(stock: StockDto) {
        val dialogBinding = DialogStockTradingBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        // 다이얼로그 ui잘리는 현상.
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.4).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // ✅ 주식명 & 현재 주가 설정
        dialogBinding.textDialogStockTitle.setText(stock.name.substringBefore(" "))
        dialogBinding.textDialogStockPrice.setText("${stock.pricePer}")
//        viewModel.ownedStockQty.observe(viewLifecycleOwner, Observer { ownedQty ->
//            dialogBinding.textDialogMyStock.setText("$ownedQty 주")
//        })

        // 매도 버튼 클릭
        dialogBinding.btnSell.setOnClickListener {
            viewModel.sellStock(stock.id, 1)
            dialog.dismiss()
        }

        // 매수 버튼
        dialogBinding.btnBuy.setOnClickListener {
            viewModel.buyStock(stock.id, 1)
            dialog.dismiss()
        }

        dialog.show()
    }





    override fun onStockClick(stock: StockDto) {
        Toast.makeText(requireContext(), "선택한 주식: ${stock.name}", Toast.LENGTH_SHORT).show()
        viewModel.selectStock(stock)
    }



    private fun initAdapter() {
        stockAdapter = StockAdapter(arrayListOf(), this)
        binding.recyclerStockList.adapter = stockAdapter
    }

    private fun observeViewModel() {
        // 어댑터 데이터 업데이트
        viewModel.stockList.observe(viewLifecycleOwner, Observer { stockList ->
            stockList?.let {
                stockAdapter.updateData(it)
            }
        })
        //매수 응답
        viewModel.buyTransaction.observe(viewLifecycleOwner) { transaction ->
            transaction?.let {
                Toast.makeText(requireContext(), "매수 완료: ${it.shareCount}주", Toast.LENGTH_SHORT).show()
            }
        }
        //ui업데이트
        viewModel.selectedStock.observe(viewLifecycleOwner) { stock ->
            stock?.let {
                binding.textHeadStockName.text = it.name.substringBefore(" ")
                binding.textHeadStockPrice.text = it.pricePer.toString()
                binding.textHeadStockChange.text = "${it.changeRate}%"
            }
        }


        // ✅ 매도 응답 처리
//        viewModel.sellTransaction.observe(viewLifecycleOwner) { transaction ->
//            transaction?.let {
//                binding.textHeadStockName.text = it.ownedQty.toString() + "주 보유 중"
//            }
//        }
        viewModel.sellTransaction.observe(viewLifecycleOwner) { transaction ->
            transaction?.let {
                Toast.makeText(requireContext(), "매도 완료: ${it.shareCount}주", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ 에러 메시지 처리
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }



}