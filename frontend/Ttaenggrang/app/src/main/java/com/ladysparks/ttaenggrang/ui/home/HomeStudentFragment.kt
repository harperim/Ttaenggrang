package com.ladysparks.ttaenggrang.ui.home

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.base.BaseTableAdapter
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.data.model.dto.TaxDto
import com.ladysparks.ttaenggrang.data.model.response.WeekReportStudentGrowth
import com.ladysparks.ttaenggrang.data.model.response.WeekReportSummaryResponse
import com.ladysparks.ttaenggrang.databinding.DialogIncomeSummaryBinding
import com.ladysparks.ttaenggrang.databinding.FragmentHomeStudentBinding
import com.ladysparks.ttaenggrang.realm.NotificationRepository
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel
import com.ladysparks.ttaenggrang.ui.component.PieChartComponent
import com.ladysparks.ttaenggrang.ui.component.PieChartComponent2
import com.ladysparks.ttaenggrang.util.CustomDateUtil
import com.ladysparks.ttaenggrang.util.NumberUtil
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import com.ladysparks.ttaenggrang.util.TransactionTypeUtil
import com.ladysparks.ttaenggrang.util.showErrorDialog


class HomeStudentFragment : BaseFragment<FragmentHomeStudentBinding>(FragmentHomeStudentBinding::bind, R.layout.fragment_home_student) {

    private lateinit var homeStudentViewModel: HomeStudentViewModel

    private lateinit var studentAdapter: BaseTableAdapter
    private lateinit var alarmAdapter: AlarmAdapter

    private lateinit var studentJob: JobDto
    private lateinit var taxesList: List<TaxDto>
    private lateinit var weekReportSummaryData: WeekReportSummaryResponse
    private lateinit var weekGrowth : WeekReportStudentGrowth
    private lateinit var weekReportAiFeedback : String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeStudentViewModel =  ViewModelProvider(this).get(HomeStudentViewModel::class.java)

        initAdapter()
        initObserve()
        initEvent()
        loadAlarmList()

        // 함수 실행
        homeStudentViewModel.fetchStudentSummary()
        homeStudentViewModel.fetchBankTransactions()
        homeStudentViewModel.fetchJobInfo()
        homeStudentViewModel.fetchTaxesList()
        homeStudentViewModel.fetchWeeklyReportSummary()
        homeStudentViewModel.fetchWeeklyGrowth()
        homeStudentViewModel.fetchWeeklyAiFeedback()
    }

    private fun initAdapter() {
        val studentHeader = listOf("거래날짜", "거래내역", "금액", "계좌 잔고")
        studentAdapter = BaseTableAdapter(studentHeader, emptyList())
        binding.recyclerTransactionHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTransactionHistory .adapter = studentAdapter
    }

    private fun initObserve() {
        homeStudentViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showErrorDialog(Throwable(it))
                homeStudentViewModel.clearErrorMessage()
            }
        }

        homeStudentViewModel.studentSummaryData.observe(viewLifecycleOwner){ response ->
            binding.textNationalRevenue.text = NumberUtil.formatWithComma(response!!.accountBalance.toString())
            binding.textAvgBalance.text = "${response.currentRank} 위"
            binding.textMyAsset.text = "${NumberUtil.formatWithComma(response.totalAsset)}"
            binding.textGoalAmount.text = "${NumberUtil.formatWithComma(response.goalAmount)}"

            // 등수, 목표 달성률 저장
            SharedPreferencesUtil.putValue(SharedPreferencesUtil.MY_RANK, response.currentRank)
            SharedPreferencesUtil.putValue(SharedPreferencesUtil.MY_ACHIEVEMENT_RATE, response.achievementRate.toInt())

            // 목표 달성 차트 재구성
            setupGoalAchievementChart(response.totalAsset.toFloat(), response.goalAmount.toFloat(), response.achievementRate.toFloat())

            // 총 자산 차트 재구성
            setupTotalAssetsChart(response.accountBalance.toFloat(), response.totalSavings.toFloat(), response.totalInvestmentAmount.toFloat())
        }

        homeStudentViewModel.bankTransactionsList.observe(viewLifecycleOwner) { responsse ->
            responsse?.let {
                val dataRows = it.map { transactions ->
                    BaseTableRowModel(
                        listOf(
                            CustomDateUtil.formatToDateTime(transactions!!.transactionDate),
                            TransactionTypeUtil.convertToKorean(transactions.transactionType),
                            NumberUtil.formatWithComma(transactions.amount),
                            NumberUtil.formatWithComma(transactions.accountBalance)
                        )
                    )
                }
                studentAdapter.updateData(dataRows)
            }
        }

        // 학생의 직업 정보
        homeStudentViewModel.studentJobInfo.observe(viewLifecycleOwner) { response ->
            studentJob = response!!
        }

        homeStudentViewModel.taxesList.observe(viewLifecycleOwner) { response ->
            // 세금 리스트 추가하고 view recyclerlist 에 띄워주기.
            taxesList = response
        }

        // 이번 주 금융 활동 요약
        homeStudentViewModel.weekReportSummary.observe(viewLifecycleOwner){ response ->
            weekReportSummaryData = response!!
        }

        // 이번주 금융 성적표
        homeStudentViewModel.weeklyGrowth.observe(viewLifecycleOwner) { response ->
            weekGrowth = response!!
        }

        // 최신 AI 피드백
        homeStudentViewModel.weekAiFeedback.observe(viewLifecycleOwner) { resources ->
            weekReportAiFeedback = resources
        }
    }

    private fun initEvent() {
        // 소득 명세서 확인
        binding.btnSalary.setOnClickListener {
            val dialogIncomeDetailBinding = DialogIncomeSummaryBinding.inflate(layoutInflater)
            val dialog = Dialog(requireContext())
            dialog.setContentView(dialogIncomeDetailBinding.root)

            // 세금 리스트 (이름 + 세율) + API
            val adapter = TaxsAdapter(taxesList)
            dialogIncomeDetailBinding.recyclerIncomeList.adapter = adapter
            dialogIncomeDetailBinding.recyclerIncomeList.layoutManager = LinearLayoutManager(requireContext())

            // 급여 정보
            val expectedSalary = studentJob.baseSalary * (1 - taxesList.sumOf { it.taxRate }) // 세금 차감 후 급여
            dialogIncomeDetailBinding.textSalary.text = NumberUtil.formatWithComma(studentJob.baseSalary)
            dialogIncomeDetailBinding.textExpectedSalary.text = NumberUtil.formatWithComma(expectedSalary.toInt())

            // event
            dialogIncomeDetailBinding.btnYes.setOnClickListener {  dialog.dismiss() }
            dialogIncomeDetailBinding.btnCancel.setOnClickListener { dialog.dismiss() }

            dialog.show()
        }

        // 주간보고서 확인
        binding.btnWeekReport.setOnClickListener {
            val dialog = WeeklyReportDialog(requireContext(), weekReportSummaryData, weekReportAiFeedback, weekGrowth)

            val displayMetrics = resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels // 화면 전체 너비(px)
            dialog.window?.setLayout((screenWidth * 0.8).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.show()
       }
    }

    private fun loadAlarmList() {
        val alarmList = NotificationRepository.getStudentNotifications()

        if(alarmList.isNullOrEmpty()){
            binding.recyclerAlarm.visibility = View.GONE
            binding.textNullAlarm.visibility = View.VISIBLE
        }else{
            binding.recyclerAlarm.visibility = View.VISIBLE
            binding.textNullAlarm.visibility = View.GONE

            alarmAdapter = AlarmAdapter(alarmList, requireContext()) // ✅ context 추가
            binding.recyclerAlarm.adapter = alarmAdapter
            binding.recyclerAlarm.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    // 1. 목표 달성률 셋팅
    private fun setupGoalAchievementChart(currentAsset: Float, goalAsset: Float, achievementRate: Float) {
        val pieChart = binding.pieChartGoal

        val safeAchievementRate = achievementRate.coerceAtMost(100f)
        val remainingPercentage = 100f - safeAchievementRate

        val entries = listOf(
            PieEntry(safeAchievementRate),
            PieEntry(remainingPercentage)
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                ContextCompat.getColor(requireContext(), R.color.chartYellow), // 노란색 (달성)
                ContextCompat.getColor(requireContext(), R.color.chartGray)    // 회색 (미달성)
            )
            valueTextSize = 0f  // 내부 값 숨기기
            setDrawValues(false) // 값 텍스트 안 보이게 설정
        }

        val pieData = PieData(dataSet)

        pieChart.apply {
            data = pieData
            description.isEnabled = false
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            isDrawHoleEnabled = true
            holeRadius = 60f
            setHoleColor(Color.WHITE)

            centerText = "${achievementRate.toInt()}%"
            setCenterTextSize(16f)
            setCenterTextColor(Color.BLACK)

            // 바깥 기본 여백을 없애기 위해 음수값 사용
            setExtraOffsets(-10f, -10f, -10f, -10f)

            legend.isEnabled = false

            animateY(1000)
        }
    }

    // 2. 파이 차트 셋팅
    private fun setupTotalAssetsChart(accountBalance: Float, totalSavings: Float, totalInvestmentAmount: Float) {
        // 데이터 설정 (뷰모델 값 반영)
        val dataList = listOf(
            accountBalance to "계좌 잔액",
            totalInvestmentAmount to "투자",
            totalSavings to "저축"
        )

        // 색상 설정
        val colorList = listOf(
            ContextCompat.getColor(requireContext(), R.color.chartBlue),  // 계좌 잔액
            ContextCompat.getColor(requireContext(), R.color.chartPink),  // 투자
            ContextCompat.getColor(requireContext(), R.color.chartPurple) // 저축
        )

        // 차트 업데이트
        setupPieChart(binding.pieChart, dataList, colorList)
    }

    private fun setupPieChart(pieChart: PieChart, dataList: List<Pair<Float, String>>, colorList: List<Int>) {
        if (dataList.isEmpty() || colorList.size < dataList.size) return

        val entries = dataList.map { PieEntry(it.first, it.second) }

        val dataSet = PieDataSet(entries, "").apply {
            colors = colorList
            valueTextSize = 14f
            valueTextColor = Color.WHITE
            setValueFormatter(PercentFormatter(pieChart))
            setDrawValues(true)
        }

        val pieData = PieData(dataSet).apply {
            setValueTextSize(12f)
            setValueTextColor(Color.WHITE)
        }

        pieChart.apply {
            data = pieData
            description.isEnabled = false
            isClickable = false
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            animateY(1000)

            // 여백 조정 → 차트가 왼쪽으로 쏠리는 문제 해결
            setExtraOffsets(10f, 10f, 60f, 10f)

            // 차트 크기 조정
            setHoleRadius(60f)  // 기존 40f → 50f (더 크게 조정)
            setTransparentCircleRadius(60f)  // 기존 50f → 55f

            // 범례 위치 조정 (중앙 정렬)
            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER // 기존 RIGHT → CENTER
                orientation = Legend.LegendOrientation.VERTICAL
                textSize = 14f
                yOffset = -50f
                setDrawInside(true)
                xOffset = 70f
            }

            requestLayout()
            invalidate()
        }
    }
}