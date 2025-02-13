package com.ladysparks.ttaenggrang.ui.stock

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.viewModels
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.data.model.dto.StockTransactionDto
import com.ladysparks.ttaenggrang.data.model.dto.StudentStockDto
import com.ladysparks.ttaenggrang.data.model.dto.TransType
import com.ladysparks.ttaenggrang.data.model.request.StudentSignInRequest
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.DialogNewsDetailBinding
import com.ladysparks.ttaenggrang.databinding.DialogStockConfirmBinding
import com.ladysparks.ttaenggrang.databinding.DialogStockTradingBinding
import com.ladysparks.ttaenggrang.databinding.FragmentStockStudentBinding
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StockStudentFragment : BaseFragment<FragmentStockStudentBinding>(
    FragmentStockStudentBinding::bind,
    R.layout.fragment_stock_student
), OnStockClickListener {

    private val viewModel: StockViewModel by viewModels()
    private lateinit var stockAdapter: StockAdapter
    private var selectedStock: StockDto? = null // 선택한 주식 저장
    private var studentId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //초기화
        initAdapter()

        // LiveData 관찰하여 데이터 변경 시 UI 업데이트
        observeViewModel()

        // 서버에서 주식 데이터 가져오기
        viewModel.fetchAllStocks()

        // 서버에서 거래 가능 현금 가져오기
        viewModel.fetchBalance()

        // studentId 가져오기 (예: SharedPreferences에서 가져오기)
        studentId = SharedPreferencesUtil.getUserId()

        // 거래버튼 클릭 이벤트 수정
        binding.btnTrade.setOnClickListener {
            lifecycleScope.launch {
                //viewModel.fetchMarketStatus() // 주식장 상태 최신화 요청
                //delay(500) // 서버 응답 대기

                // 최신 주식장 상태 확인
                //if (viewModel.isMarketActive.value == true)
                //{
                    val selectedStock = viewModel.selectedStock.value

                    selectedStock?.let { stock ->
                        val stockId = stock.id // 선택한 주식 ID 가져오기

                        // studentId가 유효한지 확인
                        if (studentId != -1) {
                            viewModel.fetchOwnedStocks(studentId) // studentId 전달
                            delay(500) // 데이터 로딩 대기

                            // 학생 보유 주식 목록에서 선택한 주식 찾기
                            val ownedStock =
                                viewModel.ownedStocks.value?.find { it.stockId == stockId }
                            val ownedQty = ownedStock?.ownedQty ?: 0 // 보유량 가져오기

                            showDialog(stock, ownedQty) // 다이얼로그 실행
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "학생 ID를 찾을 수 없습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } ?: Toast.makeText(requireContext(), "먼저 주식을 선택해주세요.", Toast.LENGTH_SHORT)
                        .show()
               // } else {
                    Toast.makeText(requireContext(), "주식 시장이 닫혀 있습니다!", Toast.LENGTH_SHORT).show()
                }
            }
        //}

        binding.btnDetail.setOnClickListener {
            Toast.makeText(requireContext(),"눌림",Toast.LENGTH_SHORT).show()
            showNews()
        }
    }

    private fun showNews() {
        val dialogNewsDetailBinding = DialogNewsDetailBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogNewsDetailBinding.root)

        // 다이얼로그 ui잘리는 현상.
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.6).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // 닫기 버튼 클릭 시 다이얼로그 닫기
        dialogNewsDetailBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //매도 매수 다이얼로그
    @SuppressLint("SetTextI18n")
    private fun showDialog(stock: StockDto, ownedQty: Int) {
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
        dialogBinding.textDialogMyStock.setText("$ownedQty 주")


        // ✅ 사용자가 거래할 주식 수 입력 시, 즉시 계산값 변경
        dialogBinding.textDialogStockTrade.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val inputAmount = s.toString().toIntOrNull() ?: 0
                val expectedPayment = stock.pricePer * inputAmount // ✅ 현재 주가 * 입력한 주식 수
                dialogBinding.textContent1.text = "$expectedPayment" //
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        dialogBinding.btnSell.setOnClickListener {
            val inputText = dialogBinding.textDialogStockTrade.text.toString().trim()
            val sellCount = inputText.toIntOrNull() ?: 0

            when {
                sellCount <= 0 -> {
                    Toast.makeText(requireContext(), "올바른 수량을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                sellCount > ownedQty -> { // ✅ 보유한 주식보다 많은 수량을 입력하면 예외 처리
                    Toast.makeText(requireContext(), "보유한 주식보다 많은 수량을 매도할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // ✅ 정상적인 경우 LiveData 업데이트 후 다이얼로그 실행
                    viewModel.updateTradeAmount(sellCount, stock.pricePer, ownedQty, TransType.SELL)
                    dialog.dismiss()
                    showConfirmDialog(stock, sellCount, TransType.SELL)
                }
            }
        }

        // 매수 버튼 구현
        dialogBinding.btnBuy.setOnClickListener {
            val inputText = dialogBinding.textDialogStockTrade.text.toString().trim()
            val buyCount = inputText.toIntOrNull() ?: 0
            val totalCost = stock.pricePer * buyCount // ✅ 총 매수 예상 비용
            val balance = viewModel.balance.value ?: 0 // ✅ 현재 보유 현금

            // 예외처리
            when {
                buyCount <= 0 -> {
                    Toast.makeText(requireContext(), "올바른 수량을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                totalCost > balance -> { // ✅ 보유 현금보다 많은 금액을 매수하려 하면 예외 처리
                    Toast.makeText(requireContext(), "보유 현금이 부족합니다.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // ✅ 정상적인 경우 LiveData 업데이트 후 다이얼로그 실행
                    viewModel.updateTradeAmount(buyCount, stock.pricePer, ownedQty, TransType.BUY)
                    dialog.dismiss()
                    showConfirmDialog(stock, buyCount, TransType.BUY)
                }
            }
        }
        dialog.show()
    }

    private fun showConfirmDialog(stock: StockDto, tradeAmount: Int, transactionType: TransType) {
        val confirmDialogBinding = DialogStockConfirmBinding.inflate(layoutInflater)
        val confirmDialog = Dialog(requireContext())
        confirmDialog.setContentView(confirmDialogBinding.root)

        // 다이얼로그 크기 조정
        confirmDialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.4).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // 다이얼로그 타이틀 설정 (매도 or 매수)
        confirmDialogBinding.textDialogTitle.text = if (transactionType == TransType.SELL) "매도 하시겠습니까?" else "매수 하시겠습니까?"

        // 예상 결제 금액
        viewModel.expectedPayment.observe(confirmDialogBinding.root.context as LifecycleOwner) { amount ->
            confirmDialogBinding.textContent1.text = "$amount" // ✅ 예상 결제 금액 표시
        }

        // 거래 후 보유 현금 표시
        viewModel.updatedBalance.observe(confirmDialogBinding.root.context as LifecycleOwner) { updatedAsset ->
            confirmDialogBinding.textContent2.text = "$updatedAsset"
        }

        // ✅ 거래 후 보유 주식 업데이트
        viewModel.updatedOwnedStock.observe(confirmDialogBinding.root.context as LifecycleOwner) { newStock ->
            if (transactionType == TransType.SELL) {
                confirmDialogBinding.textContent3.text = "${newStock} 주"

            } else {
                confirmDialogBinding.textContent3.text = "${newStock} 주"
            }
        }

        // ✅ "거래하기" 버튼 클릭 시, 매도 또는 매수 실행
        confirmDialogBinding.btnYes.setOnClickListener {
            if (transactionType == TransType.SELL) {
                viewModel.sellStock(stock.id, tradeAmount, studentId)
            } else {
                viewModel.buyStock(stock.id, tradeAmount, studentId)
            }
            confirmDialog.dismiss()
        }

        // ✅ "취소" 버튼 클릭 시 다이얼로그 닫기
        confirmDialogBinding.btnNo.setOnClickListener {
            confirmDialog.dismiss()
        }

        confirmDialog.show()
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
                Toast.makeText(requireContext(), "매수 완료: ${it.shareCount}주", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // ui업데이트
        viewModel.selectedStock.observe(viewLifecycleOwner) { stock ->
            stock?.let {
                binding.textHeadStockName.text = it.name.substringBefore(" ")
                binding.textHeadStockPrice.text = it.pricePer.toString()
                binding.textHeadStockChange.text = "${it.changeRate}%"
            }
        }

        // 매도 응답 처리
        viewModel.sellTransaction.observe(viewLifecycleOwner) { transaction ->
            transaction?.let {
                Toast.makeText(requireContext(), "매도 완료: ${it.shareCount}주", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // 에러 메시지 처리
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // 거래 가능 현금
        viewModel.balance.observe(viewLifecycleOwner) { balance ->
            binding.textTradeInfo1.text = "$balance"
        }
    }
}