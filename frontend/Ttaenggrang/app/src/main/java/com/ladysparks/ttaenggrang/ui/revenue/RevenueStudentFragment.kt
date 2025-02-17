package com.ladysparks.ttaenggrang.ui.revenue

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.databinding.FragmentRevenueStudentBinding
import kotlinx.coroutines.launch
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil




class RevenueStudentFragment : BaseFragment<FragmentRevenueStudentBinding>(FragmentRevenueStudentBinding::bind, R.layout.fragment_revenue_student) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getStudentPayments()
    }

    private fun getStudentPayments() {
        lifecycleScope.launch {
            runCatching {
                RetrofitUtil.taxService.getStudentTaxPayments()
            }.onSuccess {
                val payments = it.data ?: emptyList()
                if (payments.isNotEmpty()) {
                    binding.recyclerStudnetThisMonthTax.visibility = View.VISIBLE
                    binding.textNullNationTax.visibility = View.GONE
                    payments.forEach {
                        Log.d("test", "tax ID: ${it.taxId}, amount: ${it.amount}, status: ${it.status}")
                    }
                } else {
                    binding.textNullNationTax.visibility = View.VISIBLE
                    binding.recyclerStudnetThisMonthTax.visibility = View.GONE
                    Log.d("test", "No tax history")
                }
            }.onFailure {
                Log.d("test", "api failure")
            }
        }
    }
}