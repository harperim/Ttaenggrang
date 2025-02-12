package com.ladysparks.ttaenggrang.ui.home

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.ladysparks.ttaenggrang.MainActivity
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.base.BaseTableAdapter
import com.ladysparks.ttaenggrang.data.model.dto.AlarmDto
import com.ladysparks.ttaenggrang.databinding.DialogBaseConfirmCancelBinding
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel
import com.ladysparks.ttaenggrang.databinding.FragmentHomeTeacherBinding
import com.ladysparks.ttaenggrang.ui.component.BaseTwoButtonDialog
import com.ladysparks.ttaenggrang.util.NavigationManager
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_STUDENT_MANAGEMENT
import com.ladysparks.ttaenggrang.util.NumberUtil
import com.ladysparks.ttaenggrang.util.showToast
import java.util.Date


//class HomeFragment : Fragment() {
class HomeTeacherFragment : BaseFragment<FragmentHomeTeacherBinding>(FragmentHomeTeacherBinding::bind, R.layout.fragment_home_teacher) {

    private lateinit var homeViewModel: HomeViewModel

    // Adapter
    private lateinit var studentAdapter: BaseTableAdapter
    private lateinit var alarmAdapter: AlarmAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)


        initAdapter()
        observeLiveData()

        // 샘플 데이터
        sampleDataAlarmList()
        sampleBarChart()

        initEvent()

        // 데이터 요청
        homeViewModel.fetchEconomySummary()
        homeViewModel.fetchStudentList()
    }


    private fun observeLiveData() {
        // 1. 국가 관련 기본 필수 정보
        homeViewModel.economySummary.observe(viewLifecycleOwner) { response ->
            binding.textNationalRevenue.text = NumberUtil.formatWithComma(response.treasuryIncome.toString())
            binding.textAvgBalance.text = NumberUtil.formatWithComma(response.averageStudentBalance.toString())
            binding.textActiveProducts.text = response.activeItemCount.toString() + "개"
            binding.textSavingGoal.text = NumberUtil.formatWithComma(response.classSavingsGoal.toString())
        }

        // 2. 학생정보 리스트
        binding.recyclerStudent.visibility = View.VISIBLE
        binding.textNullStudent.visibility = View.GONE
        homeViewModel.studentList.observe(viewLifecycleOwner) { response ->
            if(response.isNullOrEmpty()){
                binding.recyclerStudent.visibility = View.GONE
                binding.textNullStudent.visibility = View.VISIBLE
                return@observe
            }

            response?.let {
                val dataRows = it.map { student ->
                    BaseTableRowModel(
                        listOf(
                            student.name?.toString() ?: "N/A",
                            student.username ?: "N/A",
                            student.job?.jobName ?: "N/A",
                            student.job?.baseSalary.toString() ?: "0",
                            student.bankAccount?.balance.toString() ?: "0"
                        )
                    )
                }
                studentAdapter.updateData(dataRows)
            }
        }

        // 3. 알람 리스트 (우선 서버 기반 가져오기 or 내부 저장소)

        // 4. 학생 평균 수입/지출 현황 그래프 데이터
    }

    private fun sampleBarChart() {
        // 데이터 (7개: 수입 & 지출)
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "June", "July")
        val incomeData = listOf(8f, 6f, 5f, 7f, 9f, 10f, 9f)  // 수입 데이터
        val expenseData = listOf(9f, 8f, 10f, 9f, 8f, 12f, 11f)  // 지출 데이터

        val incomeEntries = ArrayList<BarEntry>()
        val expenseEntries = ArrayList<BarEntry>()

        for (i in months.indices) {
            incomeEntries.add(BarEntry(i.toFloat(), incomeData[i]))  // 초록색 막대
            expenseEntries.add(BarEntry(i.toFloat(), expenseData[i]))  // 주황색 막대
        }

        // 데이터셋 생성 (수입 & 지출)
        val incomeSet = BarDataSet(incomeEntries, "수입").apply {
            color = getColor(R.color.mainGreen)  // 초록색
            setDrawValues(false)
        }

        val expenseSet = BarDataSet(expenseEntries, "지출").apply {
            color = getColor(R.color.mainOrange)  // 주황색
            setDrawValues(false)
        }

        // 색상
        incomeSet.colors =  listOf(ContextCompat.getColor(requireContext(), R.color.mainGreen))
        expenseSet.colors = listOf(ContextCompat.getColor(requireContext(), R.color.mainOrange))

        // BarData 생성 (두 개의 데이터셋 포함)
        val barData = BarData(incomeSet, expenseSet)
        barData.barWidth = 0.4f  // 막대 너비 조정

        // 차트 설정
        binding.barChart.data = barData
        binding.barChart.description.isEnabled = false  // 설명 비활성화
        binding.barChart.setFitBars(true)
        binding.barChart.animateY(1000)  // 애니메이션 적용

        // X축 설정
        val xAxis = binding.barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(months) // X축 값 변경
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)  // X축 격자선 제거
        xAxis.setCenterAxisLabels(true) // 그룹 간 간격 정렬
        xAxis.granularity = 1f  // X축 간격
        xAxis.axisMinimum = 0f  // X축 최소값 설정

        // Y축 설정
        binding.barChart.axisLeft.axisMinimum = 0f
        binding.barChart.axisRight.isEnabled = false  // 오른쪽 Y축 제거

        // 막대 그룹핑 (수입 & 지출 묶기)
        val groupSpace = 0.2f
        val barSpace = 0.05f
        val barWidth = 0.4f  // 막대 너비
        barData.barWidth = barWidth

        binding.barChart.xAxis.axisMaximum = months.size.toFloat()
        binding.barChart.groupBars(0f, groupSpace, barSpace)  // 그룹 적용
        binding.barChart.invalidate()  // 차트 갱신

        // 범례 설정
        val legend =  binding.barChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
    }

    private fun sampleDataAlarmList() {
        val tempData = listOf(
            AlarmDto(1, "거래 발생", "누가 물건을 샀어요", "시스템", Date().time),
            AlarmDto(2, "거래 발생1", "누가 물건을 샀어요2", "시스템2", Date().time),
            AlarmDto(3, "거래 발생2", "누가 물건을 샀어요3", "시스템3", Date().time)
        )

        // 어댑터 초기화 및 RecyclerView 설정
        alarmAdapter = AlarmAdapter(tempData)
        binding.recyclerAlarm.adapter = alarmAdapter
        binding.recyclerAlarm.layoutManager = LinearLayoutManager(requireContext())

        // 어댑터 데이터 갱신
        alarmAdapter.updateData(tempData)
    }

    private fun initAdapter() {
        alarmAdapter = AlarmAdapter(arrayListOf())
        binding.recyclerAlarm.adapter = alarmAdapter

        // 학생 정보 리스트
        val studentHeader = listOf("이름", "아이디", "직업", "월급", "계좌 잔고")
        studentAdapter = BaseTableAdapter(studentHeader, emptyList())
        binding.recyclerStudent.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerStudent.adapter = studentAdapter
    }


    private fun initEvent() {
        binding.btnSalary.setOnClickListener {

        }

        binding.btnAlarmMore.setOnClickListener {
            showToast("알람 내역 더보기")
        }

        binding.btnStudentMore.setOnClickListener {
            NavigationManager.moveFragment(FRAGMENT_STUDENT_MANAGEMENT)
        }
    }


}