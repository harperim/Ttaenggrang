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
import com.ladysparks.ttaenggrang.databinding.FragmentStoreTeacherBinding
import com.ladysparks.ttaenggrang.databinding.FragmentStudentsBinding
import kotlinx.coroutines.launch
import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.size.ViewSizeResolver
import com.ladysparks.ttaenggrang.databinding.DialogItemTeacherRegisterBinding
import com.ladysparks.ttaenggrang.util.showToast
import com.ladysparks.ttaenggrang.data.model.request.StoreRegisterRequest


class StoreTeacherFragment : BaseFragment<FragmentStoreTeacherBinding>(FragmentStoreTeacherBinding::bind, R.layout.fragment_store_teacher) {

    private lateinit var storeViewModel: StoreViewModel
    private lateinit var storeTeacherRegisteredItemAdapter: StoreTeacherRegisteredItemAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storeViewModel = ViewModelProvider(this).get(StoreViewModel::class.java)

        observeLiveData()

        storeViewModel.fetchStoreItemList()

        binding.btnRegisterItem.setOnClickListener {
            requestRegisterItem()
        }

        storeTeacherRegisteredItemAdapter = StoreTeacherRegisteredItemAdapter(emptyList())
        binding.recyclerRegistedItem.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRegistedItem.adapter = storeTeacherRegisteredItemAdapter

        binding.bannerImage.load(R.drawable.logo9) {
            size(ViewSizeResolver(binding.bannerImage))
            crossfade(true)
        }
    }


    private fun observeLiveData() {

        storeViewModel.itemList.observe(viewLifecycleOwner) { response ->
            val registeredItems = response?: emptyList()

            storeTeacherRegisteredItemAdapter.updateData(registeredItems)

            // 등록된 아이템 수
            binding.textCountRegisteredItem.text = "${registeredItems.size}개"
            if (registeredItems.isNotEmpty()) {
                binding.textNullRegisteredItem.visibility = View.GONE
                binding.recyclerRegistedItem.visibility = View.VISIBLE
            } else {
                binding.textNullRegisteredItem.visibility = View.VISIBLE
                binding.recyclerRegistedItem.visibility = View.GONE
            }

        }

    }


    // 아이템 등록하기 다이얼로그
    private fun requestRegisterItem() {
        val dialogBinding = DialogItemTeacherRegisterBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnClose.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnRegister.setOnClickListener {

            val itemName = dialogBinding.textLayoutContent1.editText?.text.toString().trim()
            val itemDescription = dialogBinding.textLayoutContent2.editText?.text.toString().trim()
            val itemPrice = dialogBinding.textLayoutContent3.editText?.text.toString().trim().toIntOrNull()
            val itemCount = dialogBinding.textLayoutContent4.editText?.text.toString().trim().toIntOrNull()
            // 만들어야 함

            if (itemName.isEmpty()) {
                showToast("상품명은 필수 입력 정보입니다")
                return@setOnClickListener
            }

            if (itemPrice == null || itemPrice < 0) {
                showToast("금액은 0 이상의 숫자여야 합니다")
                return@setOnClickListener
            }

            if (itemCount == null || itemCount < 0) {
                showToast("수량은 0 이상의 숫자여야 합니다")
                return@setOnClickListener
            }

            storeViewModel.registerItem(itemName, itemDescription, itemPrice, itemCount, {
                dialog.dismiss()
                showToast("등록에 성공하였습니다")
                storeViewModel.fetchStoreItemList()
                }, {
                    dialog.dismiss()
                    showToast("등록에 실패했습니다")
                }
            )
        }
        dialog.show()
    }
}