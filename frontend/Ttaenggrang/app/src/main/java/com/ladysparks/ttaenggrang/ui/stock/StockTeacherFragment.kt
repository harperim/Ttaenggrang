package com.ladysparks.ttaenggrang.ui.stock

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.data.dummy.StockDummyData
import com.ladysparks.ttaenggrang.data.model.dto.NewsDto
import com.ladysparks.ttaenggrang.data.model.dto.StockDto
import com.ladysparks.ttaenggrang.databinding.DialogNewsCreateBinding
import com.ladysparks.ttaenggrang.databinding.DialogNewsDetailBinding
import com.ladysparks.ttaenggrang.databinding.FragmentStockManageTeacherBinding
import com.ladysparks.ttaenggrang.databinding.FragmentStockTeacherBinding
import com.ladysparks.ttaenggrang.ui.component.LineChartComponent
import com.ladysparks.ttaenggrang.util.DataUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class StockTeacherFragment : BaseFragment<FragmentStockTeacherBinding>(
    FragmentStockTeacherBinding::bind,
    R.layout.fragment_stock_teacher
), OnStockClickListener {

    private val viewModel: StockViewModel by viewModels()
    private lateinit var stockAdapter: StockAdapter
    private var selectedStock: StockDto? = null // 선택한 주식 저장
    private lateinit var lineChartComponent: LineChartComponent

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //차트 가져오기
        setUpStockChart()

        //초기화
        initAdapter()

        // LiveData 관찰하여 데이터 변경 시 UI 업데이트
        observeViewModel()

        // 서버에서 주식 데이터 가져오기
        viewModel.fetchAllStocks()
        viewModel.fetchNewsList()

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

        binding.btnNewsCreate.setOnClickListener {
            createNews()
        }

        binding.btnStockManage.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StockManageTeacherFragment())
                .addToBackStack(null)
                .commit()
        }

        initData()
    }

    // 뉴스 생성 다이얼로그
    @SuppressLint("SetTextI18n")
    private fun createNews() {
        val dialogNewsCreateBinding = DialogNewsCreateBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogNewsCreateBinding.root)

        // 다이얼로그 ui잘리는 현상.
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.4).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialogNewsCreateBinding.textDialogNewsCreateDate.setText("")
        dialogNewsCreateBinding.textDialogStockName.setText("")
        dialogNewsCreateBinding.textDialogNewsTitle.setText("")
        dialogNewsCreateBinding.textDialogNewsContent.setText("")

        // create 버튼을 눌렀을 때만 서버 요청 후 UI 업데이트
        dialogNewsCreateBinding.btnCreate.setOnClickListener {
            viewModel.createNews() // 서버에 요청 보내기
        }

        // 서버 응답을 받은 후 다이얼로그 UI를 업데이트
        viewModel.newsLiveData.observe(viewLifecycleOwner) { news ->
            news?.let {
                dialogNewsCreateBinding.textDialogNewsCreateDate.setText(it.createdAt)
                dialogNewsCreateBinding.textDialogStockName.setText(it.stockName)
                dialogNewsCreateBinding.textDialogNewsTitle.setText(it.title)
                dialogNewsCreateBinding.textDialogNewsContent.setText(it.content)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                dialogNewsCreateBinding.shimmerLayout.startShimmer()
                dialogNewsCreateBinding.shimmerLayout.visibility = View.VISIBLE
                dialogNewsCreateBinding.constraintDialogBody.visibility = View.GONE
            } else {
                dialogNewsCreateBinding.shimmerLayout.stopShimmer()
                dialogNewsCreateBinding.shimmerLayout.visibility = View.GONE
                dialogNewsCreateBinding.constraintDialogBody.visibility = View.VISIBLE
            }
        }

        // 버튼구현
        dialogNewsCreateBinding.btnCancel.setOnClickListener { // 닫기
            dialog.dismiss()
        }

        dialogNewsCreateBinding.btnAdd.setOnClickListener { // fcm 알림+등록
            val title = dialogNewsCreateBinding.textDialogNewsTitle.text.toString()
            val content = dialogNewsCreateBinding.textDialogNewsContent.text.toString()
            val stockName = dialogNewsCreateBinding.textDialogStockName.text.toString()
            val createdAt =
                dialogNewsCreateBinding.textDialogNewsCreateDate.text.toString() // 생성 시간 자동 설정
            val newsType = "POSITIVE"

            if (title.isBlank() || content.isBlank() || stockName.isBlank()) {
                Toast.makeText(requireContext(), "생성하기 버튼을 눌러주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newsDto = NewsDto(
                title = title,
                content = content,
                stockName = stockName,
                createdAt = createdAt,
                newsType = newsType
            )
            viewModel.addNews(newsDto)
            viewModel.clearNewsData()
            dialog.dismiss()
        }

        // 다이얼로그 닫힐 때 LiveData 초기화
        dialog.setOnDismissListener {
            viewModel.clearNewsData()
        }

        dialog.show()
    }

    private fun initData() {
        // 화면이 변경될 때 표시할 데이터/API 를 해당 함수에서 호출합니다.
    }

    private fun initAdapter() {
        stockAdapter = StockAdapter(arrayListOf(), this)
        binding.recyclerStockList.adapter = stockAdapter
    }

    // 주식 상세 그래프
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpStockChart() {
        lineChartComponent = binding.chartStock

        viewModel.selectedStock.observe(viewLifecycleOwner) { selectedStock ->
            selectedStock?.let { stock ->
                Log.d("StockChart", "선택된 주식: ${stock.name}, ID: ${stock.id}")
                // stockId를 기반으로 더미 데이터 중 해당 주식 데이터 찾기
                val dummyStockData = StockDummyData.generateStockSampleData(stock.name, stock.id)

                //더미데이터 로그찍기
                dummyStockData.forEach { data ->
                    Log.d(
                        "TAG",
                        "setUpStockChart: 더미!!!!{${data.date}, ${data.price}"
                    )
                }

                // 최근 7일치 데이터만 가져오기
                val last7DaysStockData = dummyStockData.takeLast(7)

                // ✅ 최근 7일치 데이터 로그 출력
                last7DaysStockData.forEach { data ->
                    Log.d("StockChart", "최근 7일 데이터: ${data.date}, ${data.price}")
                }

                // ✅ 날짜 리스트 생성 (X축 레이블로 사용)
                //val dateLabels = last7DaysStockData.map { it.date }

                // ✅ 날짜 변환 (YYYY-MM-DD → MM-DD)
                val dateFormatter = DateTimeFormatter.ofPattern("MM-dd")
                val dateLabels = last7DaysStockData.map {
                    LocalDate.parse(it.date).format(dateFormatter)
                }

                // x축을 0~6으로 변환하여 그래프에 적용
                val stockHistory = last7DaysStockData.mapIndexed { index, data ->
                    Pair(index.toFloat(), data.price)
                }

                // ✅ 차트에 적용될 데이터 확인
                stockHistory.forEach { (x, y) ->
                    Log.d("StockChart", "차트 데이터: X=$x, Y=$y")
                }

                // 그래프에 데이터 적용
                lineChartComponent.setChartData(stockHistory, dateLabels, R.color.chartBlue)
            }
        }
    }

    /// 뉴스 상세
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
                binding.textHeadStockPrice.text = it.pricePerShare.toString()
                binding.textHeadStockChange.text = "${it.changeRate}%"
            }
        }

        // 최신 뉴스 클릭 시 상세 조회 및 다이얼로그 표시
        viewModel.latestNewsLiveData.observe(viewLifecycleOwner) { latestNews ->
            latestNews?.let { it ->
                binding.textNewsDate.text = DataUtil.formatDateTimeToDisplay(it.createdAt)
                binding.textNewsTitle.text = "\"${it.title}\"" // 최신 뉴스 제목
                binding.textNewsContent.text = it.content // 최신 뉴스 내용

                // ✅ 최신 뉴스 내용 클릭 시 뉴스 상세 조회 실행
                binding.textNewsContent.setOnClickListener {
                    latestNews.id?.let { latestNewsId -> viewModel.fetchNewsDetail(latestNewsId) } // ✅ 뉴스 상세 조회 요청
                }

                binding.btnDetail.setOnClickListener {
                    latestNews.id?.let { latestNewsId -> viewModel.fetchNewsDetail(latestNewsId) }
                }
            }
        }

        // ✅ 뉴스 상세 정보 가져오면 다이얼로그 띄우기
        viewModel.newsDetailLiveData.observe(viewLifecycleOwner) { newsDetail ->
            newsDetail?.let {
                showNewsDetailDialog(it) // ✅ 다이얼로그 띄우기
            }
        }
    }

    // 아이템 클릭 이벤트 처리
    override fun onStockClick(stock: StockDto) {
        Toast.makeText(requireContext(), "선택한 주식: ${stock.name}", Toast.LENGTH_SHORT).show()
        binding.textHeadStockName.text = stock.name.substringBefore(" ")
        binding.textHeadStockPrice.text = stock.pricePerShare.toString()
        binding.textHeadStockChange.text = "${stock.changeRate}%"
    }

}
