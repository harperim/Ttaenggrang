package com.ladysparks.ttaenggrang.ui.home

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.databinding.FragmentHomeStudentBinding
import com.ladysparks.ttaenggrang.databinding.FragmentStudentsBinding
import com.ladysparks.ttaenggrang.ui.component.PieChartComponent
import com.ladysparks.ttaenggrang.util.NumberUtil


class HomeStudentFragment : BaseFragment<FragmentHomeStudentBinding>(FragmentHomeStudentBinding::bind, R.layout.fragment_home_student) {

    private lateinit var homeStudentViewModel: HomeStudentViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeStudentViewModel =  ViewModelProvider(this).get(HomeStudentViewModel::class.java)

        initObserve()
//        setupGoalAchievementChart()
//        setupTotalAssetsChart()

        // 함수 실행
        homeStudentViewModel.fetchStudentSummary()
    }

    private fun initObserve() {
        homeStudentViewModel.studentSummaryData.observe(viewLifecycleOwner){ response ->
            binding.textNationalRevenue.text = NumberUtil.formatWithComma(response!!.accountBalance.toString())
            binding.textAvgBalance.text = "${response.currentRank} 위"
            binding.textMyAsset.text = "${NumberUtil.formatWithComma(response.totalAsset)}"
            binding.textGoalAmount.text = "${NumberUtil.formatWithComma(response.goalAmount)}"

            // 목표 달성 차트 재구성
            setupGoalAchievementChart(response.totalAsset.toFloat(), response.goalAmount.toFloat(), response.achievementRate.toFloat())

            // 총 자산 차트 재구성
            setupTotalAssetsChart(response.accountBalance.toFloat(), response.totalSavings.toFloat(), response.totalInvestmentAmount.toFloat())
        }
    }

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
                ContextCompat.getColor(requireContext(), R.color.chartYellow), // ✅ 노란색 (달성)
                ContextCompat.getColor(requireContext(), R.color.chartGray)    // ✅ 회색 (미달성)
            )
            valueTextSize = 0f  // ✅ 내부 값 숨기기
            setDrawValues(false) // ✅ 값 텍스트 안 보이게 설정
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

    private fun setupTotalAssetsChart(accountBalance: Float, totalSavings: Float, totalInvestmentAmount: Float) {
        val pieChartComponent = PieChartComponent(requireContext(), binding.pieChart)

        // 데이터 설정 (뷰모델 값 반영)
        val dataList = listOf(
            accountBalance to "계좌 잔액",
            totalInvestmentAmount to "투자",
            totalSavings to "저축"
        )

        // 색상 설정
        val colorList = listOf(
            R.color.chartBlue,  // 계좌 잔액
            R.color.chartPink,  // 투자
            R.color.chartPurple // 저축
        )

        // 동적 데이터 전달하여 차트 업데이트
        pieChartComponent.setupPieChart(dataList, colorList)
    }


    private fun setupTotalAssetsChart() {
        val pieChartComponent = PieChartComponent(requireContext(), binding.pieChart)

        // 데이터 설정
        val dataList = listOf(
            35f to "급여",
            50f to "투자",
            15f to "저축"
        )

        // 색상 설정
        val colorList = listOf(
            R.color.chartBlue,
            R.color.chartPink,
            R.color.chartPurple
        )

        // 동적 데이터 전달하여 차트 그리기
        pieChartComponent.setupPieChart(dataList, colorList)
    }


}