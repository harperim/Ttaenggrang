package com.ladysparks.ttaenggrang.ui.bank

import android.os.Bundle
import android.view.View
import coil.load
import coil.size.ViewSizeResolver
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.databinding.FragmentBankTeacherBinding

class BankTeacherFragment : BaseFragment<FragmentBankTeacherBinding>(FragmentBankTeacherBinding::bind, R.layout.fragment_bank_teacher) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

        binding.bannerImage.load(R.drawable.logo9){
            size(ViewSizeResolver(binding.bannerImage))
            crossfade(true)
        }
    }

    private fun initData() {
        // 화면이 변경될 때 표시할 데이터/API 를 해당 함수에서 호출합니다.
    }


}