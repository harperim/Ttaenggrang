package com.ladysparks.ttaenggrang.ui.store

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
import com.ladysparks.ttaenggrang.databinding.FragmentStoreStudentBinding
import kotlinx.coroutines.launch
import com.ladysparks.ttaenggrang.ui.component.BaseTwoButtonDialog

class StoreStudentFragment : BaseFragment<FragmentStoreStudentBinding>(
    FragmentStoreStudentBinding::bind,
    R.layout.fragment_store_student
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getStudentItemList()
        getStudentPurchaseHistory()
// 구매 성공 다이얼로그
//        val testBuying = createBuySuccessDialog()
//        testBuying.show()
//구매 실패 다이얼로그
//        val testBuyingFail = createBuyFailDialog()
//        testBuyingFail.show()
    }

    private fun getStudentItemList() {
//        lifecycleScope.launch {
//            runCatching {
//                RetrofitUtil.storeService.getStudentItemList()
//            }.onSuccess {
//                // 확인 다시
//                Log.d("Success", "${it}")
//                val itemList = it.data ?: emptyList()
//                if (itemList.isNotEmpty()) {
//                    binding.textNullItemList.visibility = View.GONE
//                    binding.recyclerItemList.visibility = View.VISIBLE
//                } else {
//                    binding.textNullItemList.visibility = View.VISIBLE
//                    binding.recyclerItemList.visibility = View.GONE
//                }
//            }.onFailure { throwable ->
//                Log.e("API Error", "Failed to fetch itemList", throwable)
//
//            }
//        }
    }

    private fun getStudentPurchaseHistory() {
//        lifecycleScope.launch {
//            runCatching {
//                RetrofitUtil.storeService.getStudentPurchaseHistory()
//            }.onSuccess {
//                Log.d("Success", "${it}")
//                val purchaseHistory = it.data ?: emptyList()
//
//                // 정확하게는 구매한 아이템 중에 구매 수량이 1 이상인 것이 있으면 아이템이 보이게 해야 한다
//                if (purchaseHistory.isNotEmpty()) {
//                    binding.textNullMyItem.visibility = View.GONE
//                    binding.recyclerMyItem.visibility = View.VISIBLE
//                } else {
//                    binding.textNullMyItem.visibility = View.VISIBLE
//                    binding.recyclerMyItem.visibility = View.GONE
//                }
//            }.onFailure { throwable ->
//                Log.e("API Error", "Failed to fetch myItem", throwable)
//            }
//        }
    }

    // 구매 성공 다이얼로그 생성 함수
    private fun createBuySuccessDialog(): BaseTwoButtonDialog {
        return BaseTwoButtonDialog(
            context = requireContext(),
            title = "구매 완료",
            message = "상품을 구매했습니다",
            positiveButtonText = "확인",
            negativeButtonText = "취소",
            statusImageResId = R.drawable.ic_alert_possible,
            showCloseButton = true,
            onPositiveClick = null,
            onNegativeClick = null,
            onCloseClick = null
        )
    }

    // 구매 실패 다이얼로그 생성 함수
    private fun createBuyFailDialog(): BaseTwoButtonDialog {
        return BaseTwoButtonDialog(
            context = requireContext(),
            title = "거래 불가",
            message = "선생님께 문의하세요",
            positiveButtonText = null,
            negativeButtonText = "취소",
            statusImageResId = R.drawable.ic_alert_impossible,
            showCloseButton = true,
            onPositiveClick = null,
            onNegativeClick = null,
            onCloseClick = null
        )
    }
}