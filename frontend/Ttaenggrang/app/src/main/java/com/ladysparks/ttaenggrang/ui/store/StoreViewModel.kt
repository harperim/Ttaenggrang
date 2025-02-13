package com.ladysparks.ttaenggrang.ui.store

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ladysparks.ttaenggrang.data.model.request.StoreBuyingRequest
import com.ladysparks.ttaenggrang.data.model.request.StoreRegisterRequest
import com.ladysparks.ttaenggrang.data.model.response.StoreStudenItemListResponse
import com.ladysparks.ttaenggrang.data.model.response.StoreStudentPurchaseHistoryResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

class StoreViewModel: ViewModel() {

    private val _itemList = MutableLiveData<List<StoreStudenItemListResponse>>(emptyList())
    val itemList: LiveData<List<StoreStudenItemListResponse>> get() = _itemList

    fun fetchStoreItemList() {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.storeService.getStudentItemList()
            }.onSuccess {
//                _itemList.value = it.data!!
                _itemList.value = it.data ?: emptyList()
            }.onFailure { throwable ->
                Log.e("StoreViewModel", "Failed to fetch item list", throwable)
                _itemList.value = emptyList()
            }
        }
    }

    private val _myItemList = MutableLiveData<List<StoreStudentPurchaseHistoryResponse>>(emptyList())
    val myItemList: LiveData<List<StoreStudentPurchaseHistoryResponse>> get() = _myItemList

    fun fetchMyItemList() {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.storeService.getStudentPurchaseHistory()
            }.onSuccess {
                _myItemList.value = it.data ?: emptyList()
            }.onFailure { throwable ->
                Log.d("StoreViewModel", "Failed to fetch my item list", throwable)
                _myItemList.value = emptyList()
            }
        }
    }

    fun useItem(itemTransactionId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.storeService.useItem(itemTransactionId)
            }.onSuccess {
                Log.d("UseItem Success", "아이템 사용 성공: $it")
                onSuccess()
            }.onFailure { throwable ->
                Log.e("UseItem Failure", "아이템 사용 실패", throwable)
            }
        }
    }

    fun buyItem(itemId: Int, quantity: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {

        val itemBuying = StoreBuyingRequest(
            itemId = itemId,
            quantity = 1
        )
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.storeService.buyItem(itemBuying)
            }.onSuccess {
                Log.d("BuyItem Success", "아이템 구매 성공: $it")
                onSuccess()
            }.onFailure { throwable ->
                Log.e("BuyItem Failure", "아이템 구매 실패", throwable)
                onFailure()
            }
        }
    }

    fun registerItem(name: String, description: String, price: Int, quantity: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {

        val itemRegister = StoreRegisterRequest (
            name = name,
            description = description,
            price = price,
            quantity = quantity
        )

        viewModelScope.launch {
            runCatching {
                RetrofitUtil.storeService.registerItem(itemRegister)
            }.onSuccess {
                Log.d("RegisterItem Success", "아이템 등록 성공: $it")
                onSuccess()
            }.onFailure { throwable ->
                Log.e("RegisterItem Failure", "아이템 등록 실패", throwable)
                onFailure()
            }
        }

    }

}