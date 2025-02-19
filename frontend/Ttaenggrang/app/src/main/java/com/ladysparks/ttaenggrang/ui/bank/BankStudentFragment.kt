package com.ladysparks.ttaenggrang.ui.bank

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.app.viewmodel.BankViewModel
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.data.model.dto.BankHistoryDto
import com.ladysparks.ttaenggrang.data.model.dto.BankItemDto
import com.ladysparks.ttaenggrang.data.model.dto.BankManageDto
import com.ladysparks.ttaenggrang.databinding.DialogAccountDetailBinding
import com.ladysparks.ttaenggrang.databinding.DialogBankProductDetailBinding
import com.ladysparks.ttaenggrang.databinding.FragmentBankStudentBinding
import com.ladysparks.ttaenggrang.util.NumberUtil


class BankStudentFragment : BaseFragment<FragmentBankStudentBinding>(
    FragmentBankStudentBinding::bind,
    R.layout.fragment_bank_student
) {

    private val viewModel: BankViewModel by viewModels()
    private lateinit var bankAdapter: BankAdapter
    private lateinit var myAccountAdapter: BankMyAccountAdapter
    private var savingsList: List<Pair<BankManageDto, BankHistoryDto?>> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //초기화
        initAdapter()
        observeViewModel()

        //서버에서 데이터 가져오기
        viewModel.fetchBankItems()
        viewModel.fetchUserSavings()
        viewModel.fetchAllBankAccounts()
        viewModel.calculateActiveDepositTotal()





        binding.btnAccountManage.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BankManageStudentFragment())
                .addToBackStack(null)
                .commit()
        }
    }


    private fun initAdapter() {
        bankAdapter = BankAdapter(emptyList()) { selectedItem ->
            Toast.makeText(requireContext(), "선택한 상품: ${selectedItem.name}", Toast.LENGTH_SHORT)
                .show()
            showItemDialog(selectedItem)
        }
        myAccountAdapter = BankMyAccountAdapter(emptyList()) { selectedItem ->
            Toast.makeText(
                requireContext(),
                "선택한 상품: ${selectedItem.second?.savingsName}",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        binding.recyclerBankProduct.apply {
            adapter = bankAdapter
        }
        binding.recyclerMyAccount.apply {
            adapter = myAccountAdapter
        }
    }

    // 은행 상품 상세 다이얼로그
    private fun showItemDialog(selectedItem: BankItemDto) {
        viewModel.fetchBankItems()

        val dialogBinding = DialogBankProductDetailBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.4).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.textDialogTitle.text = selectedItem.name
        dialogBinding.textDuration.text = "${selectedItem.durationWeeks}주"
        dialogBinding.interestRate.text = "${selectedItem.interestRate}%"
        dialogBinding.textAmount.text = "${NumberUtil.formatWithComma(selectedItem.amount)}"
        dialogBinding.textContent.text = "중도해지 이자율 : ${selectedItem.earlyInterestRate}%"

        dialogBinding.btnYes.setOnClickListener {
//            val selectedProductId = selectedItem.  // 선택한 상품 ID
//            val selectedDay = "MONDAY"  // 예제: "월요일" 가입
            val selectedName = selectedItem.name

            viewModel.subscribeToSavings(selectedName)

            dialog.dismiss()
        }

        dialogBinding.btnNo.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()

    }

    private fun observeViewModel() {
        viewModel.bankItemList.observe(viewLifecycleOwner) { bankItems ->
            bankItems?.let {
                bankAdapter.updateData(it)
            }
        }
        viewModel.bankAccountList.observe(viewLifecycleOwner) { bankAccounts ->
            bankAccounts?.let {
                myAccountAdapter.updateData(it)
            }
        }

        viewModel.subscriptionResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                Toast.makeText(requireContext(), "적금 가입 성공!", Toast.LENGTH_SHORT).show()
                viewModel.fetchAllBankAccounts() // ✅ 가입된 계좌 목록 새로고침
            }
        }

        viewModel.bankAccountList.observe(viewLifecycleOwner) { savingsList ->
            savingsList?.let {
                myAccountAdapter.updateData(it) // 데이터 변경 시 UI 갱신
                this.savingsList = it
                viewModel.calculateActiveDepositTotal() // ✅ 계좌 목록 업데이트될 때마다 재계산
            }
        }

        viewModel.activeDepositTotal.observe(viewLifecycleOwner) { totalAmount ->
            binding.textContent.text = NumberUtil.formatWithComma(totalAmount)
        }





        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }


    }
}


