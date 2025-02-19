package com.ladysparks.ttaenggrang.ui.bank

import android.os.Bundle
import android.view.View
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.databinding.FragmentBankStudentBinding


class BankStudentFragment : BaseFragment<FragmentBankStudentBinding>(
    FragmentBankStudentBinding::bind,
    R.layout.fragment_bank_student
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //초기화
        //initAdapter()

        binding.icRight.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BankManageStudentFragment())
                .addToBackStack(null)
                .commit()
        }

    }


}