package com.ladysparks.ttaenggrang.ui.store

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
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
import coil.load
import coil.size.ViewSizeResolver
import com.ladysparks.ttaenggrang.data.model.response.StoreStudenItemListResponse
import com.ladysparks.ttaenggrang.databinding.DialogItemBuyBinding
import com.ladysparks.ttaenggrang.util.showToast
import com.ladysparks.ttaenggrang.data.model.request.StoreBuyingRequest
import com.ladysparks.ttaenggrang.data.model.response.StoreStudentPurchaseHistoryResponse
import com.ladysparks.ttaenggrang.databinding.DialogItemMineBinding
import com.ladysparks.ttaenggrang.util.NumberUtil.formatWithComma
class StoreStudentFragment : BaseFragment<FragmentStoreStudentBinding>(
    FragmentStoreStudentBinding::bind,
    R.layout.fragment_store_student
) {
    private lateinit var storeViewModel: StoreViewModel

    // 구매할 수 있는 아이템 리스트 어뎁터
    private lateinit var storeStudentAdapter: StoreStudentAdapter
    // 내가 보유한 아이템 리스트 어뎁터
    private lateinit var storeStudentMyItemAdapter: StoreStudentMyItemAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰모델 연결
        storeViewModel = ViewModelProvider(this).get(StoreViewModel::class.java)

        storeViewModel.getMyAccount()

        observeLiveData()

        // 리사이클러 세팅
        setupRecyclerViews()

        // 데이터 요청
        storeViewModel.fetchStoreItemList()
        storeViewModel.fetchMyItemList()

        binding.bannerImage.load(R.drawable.logo9) {
            size(ViewSizeResolver(binding.bannerImage))
            crossfade(true)
        }

    }

    private fun setupRecyclerViews() {

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



    private fun observeLiveData() {

        storeViewModel.itemList.observe(viewLifecycleOwner) { response ->
            val itemStoreList = response ?: emptyList()

            if (itemStoreList.isNotEmpty()) {
                binding.textNullItemList.visibility = View.GONE
                binding.recyclerItemList.visibility = View.VISIBLE
                storeStudentAdapter.updateData(itemStoreList)
            } else {
                binding.textNullItemList.visibility = View.VISIBLE
                binding.recyclerItemList.visibility = View.GONE
            }
        }

        storeViewModel.myItemList.observe(viewLifecycleOwner) { response ->
            val myItemList = response ?: emptyList()
            if (myItemList.isNotEmpty()) {
                binding.textNullMyItem.visibility = View.GONE
                binding.recyclerMyItem.visibility = View.VISIBLE
                storeStudentMyItemAdapter.updateData(myItemList)
            } else {
                binding.textNullMyItem.visibility = View.VISIBLE
                binding.recyclerMyItem.visibility = View.GONE
            }
        }

        storeViewModel.myAccount.observe(viewLifecycleOwner) {response ->
            binding.textNationAmount.text = "${formatWithComma(response.balance)}"
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

        dialogBinding.btnBuy.setOnClickListener {
            storeViewModel.buyItem(item.id, 1,
                onSuccess = {
                    dialog.dismiss()
                    createBuySuccessDialog().show()
                    storeViewModel.fetchStoreItemList()
                    storeViewModel.fetchMyItemList()
                },
                onFailure = {
                    dialog.dismiss()
                    createBuyFailDialog().show()
                }
            )
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
            storeViewModel.useItem(item.id) {
                storeViewModel.fetchMyItemList()
                dialog.dismiss()
                showToast("상품이 사용되었습니다")
            }
        }
        dialog.show()
    }
}