package com.ladysparks.ttaenggrang.ui.nation

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.data.model.dto.VoteDataDto
import com.ladysparks.ttaenggrang.data.model.dto.VoteMode
import com.ladysparks.ttaenggrang.data.model.dto.VoteStatus
import com.ladysparks.ttaenggrang.data.model.response.VoteCreateRequest
import com.ladysparks.ttaenggrang.data.model.response.VoteOptionResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.DialogNationRegisterBinding
import com.ladysparks.ttaenggrang.ui.component.BaseTwoButtonDialog
import com.ladysparks.ttaenggrang.databinding.DialogVoteCreateBinding
import com.ladysparks.ttaenggrang.databinding.DialogVoteParticipationBinding
import com.ladysparks.ttaenggrang.databinding.DialogVoteStatusBinding
import com.ladysparks.ttaenggrang.databinding.FragmentNationBinding
import com.ladysparks.ttaenggrang.ui.component.DatePickerDialogHelper
import com.ladysparks.ttaenggrang.ui.component.VoteStatusDialog
import com.ladysparks.ttaenggrang.util.CustomDateUtil
import com.ladysparks.ttaenggrang.util.DataUtil
import com.ladysparks.ttaenggrang.util.DataUtil.convertDateTime
import com.ladysparks.ttaenggrang.util.ImageUtils
import com.ladysparks.ttaenggrang.util.NumberUtil
import com.ladysparks.ttaenggrang.util.PermissionUtil
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import com.ladysparks.ttaenggrang.util.showErrorDialog
import com.ladysparks.ttaenggrang.util.showToast
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import java.util.Date

class NationFragment : BaseFragment<FragmentNationBinding>(FragmentNationBinding::bind, R.layout.fragment_nation) {

    private lateinit var nationViewModel: NationViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nationViewModel = ViewModelProvider(this).get(NationViewModel::class.java)

        initSetting()
        initObserve()
        initEvent()

        nationViewModel.fetchNationData()
        nationViewModel.currentVoteInfo()
        nationViewModel.getStudentList()
    }

    private fun initObserve() {
        nationViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showErrorDialog(Throwable(it))
                nationViewModel.clearErrorMessage()
            }
        }

        // 기본 정보
        nationViewModel.nationInfoData.observe(viewLifecycleOwner) { response ->
            binding.textNationName.text = response.nationName ?: "??"
            binding.textNationCreated.text = CustomDateUtil.formatToDate(response.establishedDate ?: "")
            binding.textNationCurrency.text = response.currency
            binding.textNationPopulation.text = (response.population ?: "0").toString()
            binding.textGoal.text = NumberUtil.formatWithComma(response.savingsGoalAmount)
        }

        // 투표 현황
        nationViewModel.currentVoteInfo.observe(viewLifecycleOwner) { response ->
            if(response.voteStatus == VoteStatus.IN_PROGRESS) {
                binding.textVoteDate.text = "${CustomDateUtil.formatToDate(response.startDate)} ~ ${CustomDateUtil.formatToDate(response.endDate)}"
                binding.textVoteTitleInfo.text = response.title
            }

            if(!SharedPreferencesUtil.getValue(SharedPreferencesUtil.IS_TEACHER, false)){
                //  학생일 경우 선생님 전용 버튼 숨김
                binding.textVoteStatus.visibility = View.GONE

                // 투표 상태에 따라 접근하지 못하도록 설정
                if(response.voteStatus == VoteStatus.COMPLETED) {
                    binding.textVoteNow.visibility = View.GONE
                    binding.textVoteDate.text = "현재 진행중인 투표가 없습니다."
                    binding.textVoteTitleInfo.visibility = View.GONE
                }else if(response.voteStatus == VoteStatus.IN_PROGRESS){
                    binding.textVoteNow.visibility = View.VISIBLE
                    binding.textVoteTitleInfo.visibility = View.VISIBLE
                }
            }else{
                binding.textVoteNow.visibility = View.GONE
                binding.textVoteStatus.visibility = View.VISIBLE
            }
        }

        // (학생) 투표 참여
        nationViewModel.submitVoteData.observe(viewLifecycleOwner) { response ->
            showToast("투표 참여가 완료 되었습니다")
        }

        // 투표 생성
        nationViewModel.createVote.observe(viewLifecycleOwner) { response ->
            nationViewModel.currentVoteInfo()
            showToast("투표 생성이 완료되었습니다")
        }

        // 투표 종료
        nationViewModel.endCurrentVote.observe(viewLifecycleOwner) { response ->
            showToast(response.data.toString())
            nationViewModel.currentVoteInfo()
            binding.textVoteDate.text = "-"
            binding.textVoteTitle.text = "-"

            showNewVoteRegister()
        }
    }

    private fun initSetting() {

        // 선생님이 아닐 경우 일부기능을 보이지 않게 설정 합니다.
        if(SharedPreferencesUtil.getValue(SharedPreferencesUtil.IS_TEACHER, false)){
            binding.textVoteNow.visibility = View.GONE
            binding.textVoteStatus.visibility = View.VISIBLE

        }else{
            binding.textVoteNow.visibility = View.VISIBLE
            binding.textVoteStatus.visibility = View.GONE
//            binding.btnClassVote.visibility = View.GONE
//            binding.btnNationInfo.visibility = View.GONE
            //binding.btnGoalSavings.visibility = View.GONE
        }

        // 학급 이미지 설정
        loadImageFromPrefs()
    }


    private fun initEvent() {
        binding.constraintClassPhoto.setOnClickListener{ loadClassPhoto() }
        binding.textVoteNow.setOnClickListener { showVoteNow() }  // 학생이 '투표하러 가기' 눌렀을 때
        binding.textVoteStatus.setOnClickListener { showVoteTeacher() } // 선생님이 '투표 생성/관리' 눌렀을 때
//        binding.btnNationInfo.setOnClickListener { createNationIfoDialog() } // 국가 정보 설정 눌렀을 대
      //  binding.btnGoalSavings.setOnClickListener { createGoalDialog() } // 목표자산 설정 눌렀을 때
    }

    private fun showVoteNow() {
        // 이미 투표 참여 이력이 있다면 !
        val nowVoteId = nationViewModel.currentVoteInfo.value?.id ?: 0
        val lastVoteId = SharedPreferencesUtil.getValue(SharedPreferencesUtil.VOTE_HISTORY_ID, -1)

        // 동일 투표 항목에 참여한 기록이 있는 경우 중복 참여 방지
        if(nowVoteId == lastVoteId){
            showToast("투표 참여 이력이 존재합니다.")
            showVoteStatus()
            return
        }

        // 투표 참여
        val dialogBinding = DialogVoteParticipationBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // 내용 구성
        val startDate = CustomDateUtil.formatToDate(nationViewModel.currentVoteInfo.value!!.startDate)
        val endDate = CustomDateUtil.formatToDate(nationViewModel.currentVoteInfo.value!!.endDate)
        dialogBinding.textVoteTitle.text = nationViewModel.currentVoteInfo.value!!.title
        dialogBinding.textVoteDate.text = "${startDate} ~ $endDate"

        // Spinner 설정
        val spinner = dialogBinding.spinnerSelectStudent
        val studentList = nationViewModel.studentList.value ?: emptyList()

        // ArrayAdapter에 직접 `VoteOptionResponse` 리스트를 전달
        val adapter = object : ArrayAdapter<VoteOptionResponse>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            studentList
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val textView = super.getView(position, convertView, parent) as TextView
                textView.text = studentList[position].studentName// 학생 이름만 표시
                return textView
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val textView = super.getDropDownView(position, convertView, parent) as TextView
                textView.text = studentList[position].studentName // 드롭다운에도 학생 이름만 표시
                return textView
            }
        }
        spinner!!.adapter = adapter

        // Button
        dialogBinding.btnDialogCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnDialogConfirm.setOnClickListener {
            val position = spinner.selectedItemPosition
            val selectedStudentID = studentList[position].voteItemId

            nationViewModel.submitVote(selectedStudentID)
            SharedPreferencesUtil.putValue(SharedPreferencesUtil.VOTE_HISTORY_ID, nowVoteId)
            dialog.dismiss()
        }

        dialog.show()
    }

    // 선생님용 : 투표현황 확인/새 투표 생성
    private fun showVoteTeacher(){
        val dialog = BaseTwoButtonDialog(
            context = requireContext(),
            title = "국민 투표",
            message = "반 학생들이 직접 평가하고\n공정하게 인센티브를 받을 수 있도록 투표로 결정합니다",
            positiveButtonText = "새 투표 생성",
            negativeButtonText = "현재 투표 현황",
            statusImageResId = R.drawable.ic_vote,
            showCloseButton = true,
            onNegativeClick = { showVoteStatus() },
            onPositiveClick = { showVoteCreate() },
            onCloseClick = {}
        )
        dialog.show()
    }

    // 투표 진행상황 확인
    private fun showVoteStatus() {
        val voteInfo = nationViewModel.currentVoteInfo.value ?: return
        VoteStatusDialog(requireContext(), voteInfo).show()
    }

    private fun showVoteCreate() {
        if(nationViewModel.currentVoteInfo.value != null && nationViewModel.currentVoteInfo.value!!.voteStatus == VoteStatus.IN_PROGRESS){
            val dialog = BaseTwoButtonDialog(
                context = requireContext(),
                title = "진행중인 투표가 존재합니다!",
                message = "현재 진행주인 투표가 있습니다\n투표를 종료하고 새 투표를 생성하시겠습니까?",
                positiveButtonText = "취소",
                negativeButtonText = "투표 종료",
                statusImageResId = R.drawable.ic_warning,
                showCloseButton = true,
                onPositiveClick = { showToast("취소") },
                onNegativeClick = { nationViewModel.endCurrentVote() },
                onCloseClick = {}
            )
            dialog.show()
        }else{
            showNewVoteRegister()
        }

    }

    // 새 투표 생성 다이얼로그
    private fun showNewVoteRegister() {
        var selectedStartDate: String? = null
        var selectedEndDate: String? = null

        val dialogBinding = DialogVoteCreateBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // Radio Button
        dialogBinding.radioStudentMode.buttonTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.radio_button_selector)
        dialogBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radioStudentMode -> {
                    dialogBinding.textRadioStudentMode.visibility = View.VISIBLE
                    dialogBinding.textRadioSelectMode.visibility = View.GONE
                }
                R.id.radioSelectMode -> {
                    dialogBinding.textRadioStudentMode.visibility = View.GONE
                    dialogBinding.textRadioSelectMode.visibility = View.VISIBLE
                }
            }
        }

        // 투표 시작 기간 : 현재보다 이전 날짜를 설정 할 수 없다.


        // 투표 종료 기간
        dialogBinding.etStartDate.setOnClickListener {
            DatePickerDialogHelper.showDatePickerDialog(requireContext(), isStartTime = true) { selectedDate ->
                selectedStartDate = selectedDate // 변수에 값 저장
                dialogBinding.etStartDate.setText(selectedDate) // EditText에 표시
                Log.d("TAG", "showNewVoteRegister: 값 테스트 ${selectedDate}")
            }
        }

        dialogBinding.etEndDate.setOnClickListener {
            DatePickerDialogHelper.showDatePickerDialog(requireContext(), isStartTime = false) { selectedDate ->
                selectedEndDate = selectedDate // 변수에 값 저장
                dialogBinding.etEndDate.setText(selectedDate) // EditText에 표시
                Log.d("TAG", "showNewVoteRegister: 값 테스트 ${selectedEndDate}")
            }
        }

        // Button
        dialogBinding.btDialogCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnDialogConfirm.setOnClickListener {
            val startDate = selectedStartDate?.let { convertDateTime(it) as? Date }
            val endDate = selectedEndDate?.let { convertDateTime(it) as? Date }

            // Date → 서버에 보낼 ISO 8601 형식으로 변환
            val serverStartDate = startDate?.let { convertDateTime(it, forDisplay = false) } as? String
            val serverEndDate = endDate?.let { convertDateTime(it, forDisplay = false) } as? String

            val createData = VoteCreateRequest(
                title = dialogBinding.editAddName.text.toString(),
                startDate = serverStartDate ?: "", // 변환된 값 사용
                endDate = serverEndDate ?: "",
                voteMode = VoteMode.STUDENT
            )

            nationViewModel.createVote(createData)
            dialog.dismiss()
        }
        // Start
        dialog.show()
    }

    private fun uploadImageToServer(imageUri: Uri) {
        lifecycleScope.launch {
            runCatching {
                 //RetrofitUtil.authService.loginTeacher(TeacherSignInRequest(email = "aa@aa.com", password = "1234"))

                // Type? : MultipartBody.Part
            }.onSuccess {
                showToast("학급 사진 저장 완료")
            }.onFailure { error ->
                showToast("학급사진 저장 실패 ${error}")
            }
        }
    }

    private fun loadClassPhoto() {
        pickImageLauncher?.launch("image/*")
    }

    private fun createGoalDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_goal_saving, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val etTargetAmount = dialogView.findViewById<EditText>(R.id.editGoalSaving)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val targetAmount = etTargetAmount.text.toString()

            if (targetAmount.isEmpty()) {
                showToast("목표 금액을 입력해주세요.")
                return@setOnClickListener
            }

            // 수정 : 목표 금액 설정 API 추가

            dialog.dismiss()  // 다이얼로그 닫기
        }

        dialog.show()

    }

    private fun createNationIfoDialog(){
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nation_info, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val textNationStartDate = dialogView.findViewById<TextView>(R.id.textNationStartDate)
        val textNationMoneyLabel = dialogView.findViewById<TextView>(R.id.textNationMoneyLabel)
        val editStudentCount = dialogView.findViewById<EditText>(R.id.editStudentCount)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val targetAmount = editStudentCount.text.toString()

            if (targetAmount.isEmpty()) {
                showToast("목표 금액을 입력해주세요.")
                return@setOnClickListener
            }

            // 수정 : 목표 금액 설정 API 추가

            dialog.dismiss()  // 다이얼로그 닫기
        }

        dialog.show()

    }


    /**
     * 학급 이미지 등록 함수
     */
    private var imageUri: Uri? = null
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.imgClassPhoto.visibility = View.VISIBLE
            binding.textClassPhotoNull.visibility = View.GONE
            binding.imgClassPhoto.setImageURI(it) // ✅ 선택한 이미지 표시

            saveImageToPrefs(it) // ✅ 유틸을 활용한 저장
            showToast("이미지 저장 완료!")
        }
    }

    private fun saveImageToPrefs(uri: Uri) {
        val bitmap = ImageUtils.uriToBitmap(requireContext(), uri) // ✅ 유틸 사용
        val encodedImage = ImageUtils.bitmapToBase64(bitmap) // ✅ Base64 변환
        SharedPreferencesUtil.putValue("NationPhoto", encodedImage) // ✅ 저장
    }

    private fun loadImageFromPrefs() {
        val savedBase64 = SharedPreferencesUtil.getValue("NationPhoto", "")
        if (!savedBase64.isNullOrEmpty()) {
            val bitmap = ImageUtils.base64ToBitmap(savedBase64) // ✅ Base64 → Bitmap 변환
            binding.imgClassPhoto.visibility = View.VISIBLE
            binding.textClassPhotoNull.visibility = View.GONE
            binding.imgClassPhoto.setImageBitmap(bitmap) // ✅ 이미지 표시
        }
    }

}