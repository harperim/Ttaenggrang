package com.ladysparks.ttaenggrang.ui.store

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.FragmentStoreStudentBinding
import kotlinx.coroutines.launch
import com.ladysparks.ttaenggrang.ui.component.BaseTwoButtonDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.ladysparks.ttaenggrang.data.model.response.StoreStudenItemListResponse
import com.ladysparks.ttaenggrang.databinding.DialogItemBuyBinding
import com.ladysparks.ttaenggrang.util.showToast
import com.ladysparks.ttaenggrang.data.model.request.StoreBuyingRequest
import com.ladysparks.ttaenggrang.data.model.response.StoreStudentPurchaseHistoryResponse
import com.ladysparks.ttaenggrang.databinding.DialogItemMineBinding

class StoreStudentFragment : BaseFragment<FragmentStoreStudentBinding>(
    FragmentStoreStudentBinding::bind,
    R.layout.fragment_store_student
) {
    // 구매할 수 있는 아이템 리스트 어뎁터
    private lateinit var storeStudentAdapter: StoreStudentAdapter
    // 내가 보유한 아이템 리스트 어뎁터
    private lateinit var storeStudentMyItemAdapter: StoreStudentMyItemAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getStudentItemList()
        getStudentPurchaseHistory()


       // 구매 가능한 아이템 리사이클러
        binding.recyclerItemList.layoutManager = GridLayoutManager(requireContext(), 3)

        // 구매 아이템 클릭 시 구매 다이얼로그
        storeStudentAdapter = StoreStudentAdapter(emptyList()) { selectedItem ->
            showBuyDialog(selectedItem)
        }

        binding.recyclerItemList.adapter =  storeStudentAdapter

        // 내가 보유한 아이템 리사이클러
        binding.recyclerMyItem.layoutManager = LinearLayoutManager(requireContext())

        // 내 보유 아이템 클릭 시 상세 다이얼로그
        storeStudentMyItemAdapter = StoreStudentMyItemAdapter(emptyList()) { selectedItem ->
            showItemDetailDialog(selectedItem)
        }

        binding.recyclerMyItem.adapter = storeStudentMyItemAdapter

    }

//    구매할 수 있는 아이템 목록 불러오기
    private fun getStudentItemList() {
        lifecycleScope.launch {
            runCatching {
                RetrofitUtil.storeService.getStudentItemList()
            }.onSuccess {
                Log.d("BuyingList Sucess", "${it}")
                val itemList = it.data ?: emptyList()
                if (itemList.isNotEmpty()) {
                    binding.textNullItemList.visibility = View.GONE
                    binding.recyclerItemList.visibility = View.VISIBLE
                    storeStudentAdapter.updateData(itemList)
                } else {
                    binding.textNullItemList.visibility = View.VISIBLE
                    binding.recyclerItemList.visibility = View.GONE
                }
            }.onFailure { throwable ->
                Log.e("BuyingList Failure", "Failed to fetch itemList", throwable)

            }
        }
    }

//    구매하고 사용하지 않은 내 보유 아이템 목록 불러오기
    private fun getStudentPurchaseHistory() {
        lifecycleScope.launch {
            runCatching {
                RetrofitUtil.storeService.getStudentPurchaseHistory()
            }.onSuccess {
                Log.d("PurchaseHistory Success", "${it}")
                val myItemList = it.data ?: emptyList()

                // 정확하게는 구매한 아이템 중에 구매 수량이 1 이상인 것이 있으면 아이템이 보이게 해야 한다
                if (myItemList.isNotEmpty()) {
                    binding.textNullMyItem.visibility = View.GONE
                    binding.recyclerMyItem.visibility = View.VISIBLE
                    storeStudentMyItemAdapter.updateData(myItemList)
                } else {
                    binding.textNullMyItem.visibility = View.VISIBLE
                    binding.recyclerMyItem.visibility = View.GONE
                }
            }.onFailure { throwable ->
                Log.e("PurchaseHistory Failure", "Failed to fetch myItem", throwable)
            }
        }
    }

    // 구매 다이얼로그
    private fun showBuyDialog(item: StoreStudenItemListResponse) {
        val dialogBinding = DialogItemBuyBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()
        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }

        dialogBinding.textItemName.text = item.name
        dialogBinding.textItemDescription.text = item.description
        dialogBinding.textItemPrice.text = "${item.price}원"

        val itemBuying = StoreBuyingRequest(
            itemId = item.id,
            quantity = 1
        )

        dialogBinding.btnBuy.setOnClickListener {
            lifecycleScope.launch {
                runCatching {
//                    Log.d("BuyingTest", "${itemBuying}")
                    RetrofitUtil.storeService.buyItem(itemBuying)
                }.onSuccess {
                    Log.d("Buying Success", "${it}")
                    createBuySuccessDialog().show()
                    // 학생이 구매 후 구매할 수 있는 아이템 리사이클러와 내 보유 아이템 리사이클러를 새로고침
                    getStudentPurchaseHistory()
                    getStudentItemList()
                }.onFailure { throwable ->
                    Log.e("Buying Failure", "Item Buying Failure", throwable)
                    createBuyFailDialog().show()
                }
            }
            dialog.dismiss()
        }
        dialog.show()

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

    // 보유 아이템 상세 다이얼로그
    private fun showItemDetailDialog(item: StoreStudentPurchaseHistoryResponse) {
        val dialogBinding = DialogItemMineBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogBinding.root).create()

        dialogBinding.btnCancel.setOnClickListener{ dialog.dismiss() }
        dialogBinding.btnClose.setOnClickListener{ dialog.dismiss() }

        dialogBinding.textItemName.text = item.itemName
        if (item.itemDescription == null || item.itemDescription.isEmpty()) {
            dialogBinding.textItemDescription.text = ""
        } else {
            dialogBinding.textItemDescription.text = item.itemDescription
        }

        dialogBinding.btnUse.setOnClickListener{
            // 사용하기 구현


        }
        dialog.show()
    }

}