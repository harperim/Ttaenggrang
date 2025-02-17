package com.ladysparks.ttaenggrang.ui.revenue

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.FragmentRevenueTeacherBinding
import kotlinx.coroutines.launch




class RevenueTeacherFragment : BaseFragment<FragmentRevenueTeacherBinding>(FragmentRevenueTeacherBinding::bind, R.layout.fragment_revenue_teacher) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getTeacherTaxInfo()
        getStudentTaxPaymentsTeacher()
    }

    private fun getTeacherTaxInfo() {
        lifecycleScope.launch {
            runCatching {
                RetrofitUtil.taxService.getTeacherTaxInfo()
            }.onSuccess {
                val taxInfos = it.data?: emptyList()
                if (taxInfos.isNotEmpty()) {
                    binding.textNullTaxInfo.visibility = View.GONE
                    binding.recyclerTaxInfo.visibility = View.VISIBLE
                } else {
                    binding.textNullTaxInfo.visibility = View.VISIBLE
                    binding.recyclerTaxInfo.visibility = View.GONE
                }
            }.onFailure { throwable ->
                Log.e("API Error", "Failed to fetch tax info", throwable)
            }
        }
    }

    private fun getStudentTaxPaymentsTeacher() {
        lifecycleScope.launch {
            runCatching {
                RetrofitUtil.taxService.getStudentTaxPaymentsTeacher()
            }.onSuccess {
                val studentsTaxAmounts = it.data?: emptyList()
                if (studentsTaxAmounts.isNotEmpty()) {
                    binding.textNullTeacherStudentTaxHistory.visibility = View.GONE
                    binding.recyclerTeacherStudentTaxHistory.visibility = View.VISIBLE
                } else{
                    binding.textNullTeacherStudentTaxHistory.visibility = View.VISIBLE
                    binding.recyclerTeacherStudentTaxHistory.visibility = View.GONE
                }
            }.onFailure { throwable ->
                Log.e("API Error", "Failed to fetch student tax payments", throwable)
            }
        }
    }
}