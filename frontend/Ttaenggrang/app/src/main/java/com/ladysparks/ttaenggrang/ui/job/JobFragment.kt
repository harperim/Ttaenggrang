package com.ladysparks.ttaenggrang.ui.job

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.base.BaseTableAdapter
import com.ladysparks.ttaenggrang.data.model.dto.JobDto
import com.ladysparks.ttaenggrang.databinding.DialogIncentiveBinding
import com.ladysparks.ttaenggrang.databinding.DialogJobRegisterBinding
import com.ladysparks.ttaenggrang.databinding.FragmentJobBinding
import com.ladysparks.ttaenggrang.ui.component.BaseTableRowModel
import com.ladysparks.ttaenggrang.util.showErrorDialog
import com.ladysparks.ttaenggrang.util.showToast

class JobFragment : BaseFragment<FragmentJobBinding>(FragmentJobBinding::bind, R.layout.fragment_job) {

    // ViewModel
    private lateinit var jobViewModel: JobViewModel

    // 직업 정보 리스트
    // BaseTableRowModel 사용법 2 :  index, clickEvent 있는 버전
    private lateinit var jobAdapter: BaseTableAdapter
    private val jobTableHeader = listOf("직업명", "직업설명", "기본급", "인원제한")

    // 직업 등록 Dialog
    private lateinit var registerDialog: AlertDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jobViewModel = ViewModelProvider(this).get(JobViewModel::class.java)

        initAdapter()
        initObserver()
        initEvent()

        // Adapter, Observer 설정이 끝난 이후, 데이터 요청 실행
        jobViewModel.fetchJobList()
    }


    private fun initAdapter() {
        val isRowClickable = true

        jobAdapter = if (isRowClickable){
            BaseTableAdapter(jobTableHeader, emptyList()){ rowIndex, rowData ->
//                showToast("${rowIndex} Click Event !")
            }
        } else{
            BaseTableAdapter(jobTableHeader, emptyList())
        }
        binding.recyclerJob.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerJob.adapter = jobAdapter
    }

    private fun initObserver() {
        jobViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                showErrorDialog(Throwable(it))
                jobViewModel.clearErrorMessage()
            }
        }
        // 1. observer :직업 정보 리스트
        jobViewModel.jobList.observe(viewLifecycleOwner) { jobList ->

            if(jobList.isNullOrEmpty()){
                binding.recyclerJob.visibility = View.GONE
                binding.textNullJob.visibility = View.VISIBLE
                return@observe
            }

            binding.recyclerJob.visibility = View.VISIBLE
            binding.textNullJob.visibility = View.GONE

            // BaseTableRowModel 사용법 2
            val jobList = jobList.mapIndexed { index, job ->
                BaseTableRowModel(
                    listOf(
                        job.jobName ?: "",
                        job.jobDescription?: "",
                        job.baseSalary.toString() ?: "",
                        job.maxPeople.toString() ?: ""
                    )
                )
            }

            // 5. Adapter 에 신규 데이터 업데이트
            jobAdapter.updateData(jobTableHeader, jobList)
        }

        // 2. observer 직업 등록 요청
        jobViewModel.registerJobResult.observe(viewLifecycleOwner) { response ->
            if(response != null){
                jobViewModel.fetchJobList()
                registerDialog.dismiss()
            }else{
                showToast("Error !")
            }

        }
    }

    // 기본 Button Evetn 정의
    private fun initEvent() {
        binding.btnRegisterJob.setOnClickListener {  requestRegisterJob() }
    }

    private fun requestRegisterJob() {
        val dialogBinding = DialogJobRegisterBinding.inflate(layoutInflater)
        registerDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnClose.setOnClickListener { registerDialog.dismiss() }
        dialogBinding.btnCancel.setOnClickListener { registerDialog.dismiss() }
        dialogBinding.btnRegister.setOnClickListener {

            // 객체 생성
            val jobData = JobDto(
                jobName = dialogBinding.editJobName.text.toString(),
                jobDescription = dialogBinding.editJobDescription.text.toString(),
                baseSalary = dialogBinding.editBaseSalary.text.toString().toInt(),
                maxPeople = dialogBinding.editMaxPeople.text.toString().toInt()
            )

            // 등록 요청
            jobViewModel.registerJob(jobData)

        }
        registerDialog.show() // 다이얼로그 띄우기
    }

}