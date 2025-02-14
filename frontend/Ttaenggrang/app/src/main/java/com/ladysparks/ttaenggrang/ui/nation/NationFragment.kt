package com.ladysparks.ttaenggrang.ui.nation

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
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
import com.ladysparks.ttaenggrang.databinding.DialogNationRegisterBinding
import com.ladysparks.ttaenggrang.ui.component.BaseTwoButtonDialog
import com.ladysparks.ttaenggrang.databinding.DialogVoteCreateBinding
import com.ladysparks.ttaenggrang.databinding.DialogVoteParticipationBinding
import com.ladysparks.ttaenggrang.databinding.DialogVoteStatusBinding
import com.ladysparks.ttaenggrang.databinding.FragmentNationBinding
import com.ladysparks.ttaenggrang.ui.component.DatePickerDialogHelper
import com.ladysparks.ttaenggrang.util.DataUtil
import com.ladysparks.ttaenggrang.util.DataUtil.convertDateTime
import com.ladysparks.ttaenggrang.util.NumberUtil
import com.ladysparks.ttaenggrang.util.PermissionUtil
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import com.ladysparks.ttaenggrang.util.showErrorDialog
import com.ladysparks.ttaenggrang.util.showToast
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import java.util.Date

class NationFragment : BaseFragment<FragmentNationBinding>(FragmentNationBinding::bind, R.layout.fragment_nation) {

    private lateinit var permissionChecker: PermissionUtil

    private lateinit var nationViewModel: NationViewModel

    // 갤러리에서 이미지 선택 후 처리하는 launcher
//    private lateinit var uploadButton: Button
//    private var imageUri: Uri? = null
//    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        uri?.let {
//            imageUri = it
//            binding.imgClassPhoto.visibility = View.VISIBLE
//            binding.textClassPhotoNull.visibility = View.GONE
//            binding?.imgClassPhoto?.setImageURI(it) // ✅ 선택한 이미지 표시
//            uploadImageToServer(imageUri!!)
//        }
//    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nationViewModel = ViewModelProvider(this).get(NationViewModel::class.java)

        initSetting()
        initObserve()
        initEvent()

        nationViewModel.fetchNationData()
        nationViewModel.currentVoteInfo()
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
            val parseDate = DataUtil.formatDateTimeFromServer(response.establishedDate.toString())
            val formattedDate = DataUtil.formatDate(parseDate!!)

            binding.textNationName.text = response.nationName ?: "??"
            binding.textNationCreated.text = formattedDate
            binding.textNationCurrency.text = response.currency
            binding.textNationPopulation.text = (response.population ?: "0").toString()
            binding.textGoal.text = NumberUtil.formatWithComma(response.savingsGoalAmount)
        }

        // 투표 현황
        nationViewModel.currentVoteInfo.observe(viewLifecycleOwner) { response ->
            var parseDate = DataUtil.formatDateTimeFromServer(response.startDate.toString())
            val startDate = DataUtil.formatDate(parseDate!!)
            parseDate = DataUtil.formatDateTimeFromServer(response.endDate.toString())
            val endDate = DataUtil.formatDate(parseDate!!)

            if(response.voteStatus == VoteStatus.IN_PROGRESS) {
                binding.textVoteDate.text = "${startDate} ~ ${endDate}"
                binding.textVoteTitleInfo.text = response.title
            }
        }

        // 투표 종료
        nationViewModel.endCurrentVote.observe(viewLifecycleOwner) { response ->
            showToast(response.data.toString())
            nationViewModel.currentVoteInfo()
            binding.textVoteDate.text = "-"
            binding.textVoteTitle.text = "-"

            showNewVoteRegister()
        }

        nationViewModel.createVote.observe(viewLifecycleOwner) { response ->
            nationViewModel.currentVoteInfo()
            showToast("투표 생성이 완료되었습니다")
        }

    }

    private fun initSetting() {

        // 1. 국가 정보에 대한 데이터가 없을 경우, 정보를 추가하라는 팝업이 먼저 보여져야 한다. && 선생님일 때
        if(false){
            val dialog = BaseTwoButtonDialog(
                context = requireContext(),
                statusImageResId = R.drawable.ic_warning,
                showCloseButton = false,
                title = "등록된 국가 정보가 존재하지 않습니다!",
                message = "화면을 눌러 국가 정보를 추가해보세요",
                positiveButtonText = "확인",
                onPositiveClick = {
                    showNationRegistration()
                }
            )
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()


            return
        }

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
            binding.constraintClassPhoto.apply {
                isClickable = false
                isEnabled = false
            }
        }
    }

    private fun showNationRegistration() {
        val dialogBinding = DialogNationRegisterBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()


//        dialogBinding.textVoteTitle.text = "우리반 봉사왕"
//        dialogBinding.textVoteDate.text = "2021.02.03 ~ 2025.03.02"
//
//        // Button
//        dialogBinding.btnDialogCancel.setOnClickListener { dialog.dismiss() }
//        dialogBinding.btnDialogConfirm.setOnClickListener {
//            showToast("투표가 완료되었습니다.")
//            dialog.dismiss()
//        }

        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()
    }

//    private fun initData() {
//        lifecycleScope.launch {
//            runCatching {
//                // 국가 정보. 목표 자산, 투표현황 등의 내용을가져옴
//                // RetrofitUtil.authService.loginTeacher(TeacherSignInRequest(email = "aa@aa.com", password = "1234"))
//            }.onSuccess {
//                // 학급 이미지 처리 로직
//                if(true){
//                    // 학급 사진이 있는 경우
//                }else{
//                    // 학급 사진이 없는 경우
//                    binding.imgClassPhoto.visibility = View.GONE
//                    binding.textClassPhotoNull.visibility= View.VISIBLE
//                }
//
//            }.onFailure { error ->
//                showToast("학급정보 불러오기 실패 ${error}")
//            }
//        }
//    }

    private fun initEvent() {
        binding.constraintClassPhoto.setOnClickListener{ loadClassPhoto() }
        binding.textVoteNow.setOnClickListener { showVoteNow() }  // 학생이 '투표하러 가기' 눌렀을 때
        binding.textVoteStatus.setOnClickListener { showVoteTeacher() } // 선생님이 '투표 생성/관리' 눌렀을 때
//        binding.btnNationInfo.setOnClickListener { createNationIfoDialog() } // 국가 정보 설정 눌렀을 대
      //  binding.btnGoalSavings.setOnClickListener { createGoalDialog() } // 목표자산 설정 눌렀을 때
    }
    private fun showVoteNow() {
        lifecycleScope.launch {
            runCatching {
                // API 호출
            }.onSuccess {
                // 결과

                // 이미 투표에 참여한 기록이 있는 경우, 재참여 할 수 없다.
                if(false){
                    showVoteStatus()
                }else{
                    // 투표 참여가능
                    val dialogBinding = DialogVoteParticipationBinding.inflate(layoutInflater)
                    val dialog = AlertDialog.Builder(requireContext())
                        .setView(dialogBinding.root)
                        .create()
                    // Spinner
                    val jddddob = dialogBinding.selectStudent
                    // job 설정
                    val studentList = arrayOf("학생 1", "학생 2", "학생 3","학생 2", "학생 3", "학생 4", "학생 5","학생 1", "학생 2", "학생 3", "학생 4", "학생 5","학생 1", "학생 2", "학생 3", "학생 4", "학생 5")
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, studentList)
                    jddddob!!.adapter = adapter

                    dialogBinding.textVoteTitle.text = "우리반 봉사왕"
                    dialogBinding.textVoteDate.text = "2021.02.03 ~ 2025.03.02"

                    // Button
                    dialogBinding.btnDialogCancel.setOnClickListener { dialog.dismiss() }
                    dialogBinding.btnDialogConfirm.setOnClickListener {
                        showToast("투표가 완료되었습니다.")
                        dialog.dismiss()
                    }

                    dialog.show()
                }
            }.onFailure {

            }
        }

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

        if(nationViewModel.currentVoteInfo.value!!.voteStatus == VoteStatus.COMPLETED){
            showToast("현재 진행중인 투표가 없습니다 !")
            return
        }

        val totalPeople = nationViewModel.currentVoteInfo.value!!.totalStudents;

        var parseDate = DataUtil.formatDateTimeFromServer(nationViewModel.currentVoteInfo.value!!.startDate)
        val startDate = DataUtil.formatDate(parseDate!!)
        parseDate = DataUtil.formatDateTimeFromServer(nationViewModel.currentVoteInfo.value!!.endDate)
        val endDate = DataUtil.formatDate(parseDate!!)

        val dialogBinding = DialogVoteStatusBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // Info
        dialogBinding.textVoteTitle.text = nationViewModel.currentVoteInfo.value!!.title
        dialogBinding.textVoteDate.text = "${startDate} ~ ${endDate}"
        dialogBinding.textVoteTotal.text = nationViewModel.currentVoteInfo.value!!.students.toString()

        // 1등
        dialogBinding.textRankFirstName.text = nationViewModel.currentVoteInfo.value!!.topRanks!!.get(0).studentName
        dialogBinding.tvVoteCountFirst.text = nationViewModel.currentVoteInfo.value!!.topRanks!!.get(0).votes.toString() + " 명"
        dialogBinding.progressBarFirst.progress = calculateProgress(nationViewModel.currentVoteInfo.value!!.topRanks!!.get(0).votes, totalPeople!!)

        // 2등
        dialogBinding.textRankSecondName.text = nationViewModel.currentVoteInfo.value!!.topRanks!!.get(1).studentName
        dialogBinding.tvVoteCountSecond.text = nationViewModel.currentVoteInfo.value!!.topRanks!!.get(1).votes.toString() + " 명"
        dialogBinding.progressBarSecond.progress = calculateProgress(nationViewModel.currentVoteInfo.value!!.topRanks!!.get(1).votes, totalPeople!!)

        // 3등
        dialogBinding.textRankThirdName.text = nationViewModel.currentVoteInfo.value!!.topRanks!!.get(2).studentName
        dialogBinding.tvVoteCountThird.text = nationViewModel.currentVoteInfo.value!!.topRanks!!.get(2).votes.toString() + " 명"
        dialogBinding.progressBarThird.progress = calculateProgress(nationViewModel.currentVoteInfo.value!!.topRanks!!.get(2).votes, totalPeople!!)

        // 총 투표 참여 인원
        dialogBinding.textVoteTotal.text = nationViewModel.currentVoteInfo.value!!.totalVotes.toString() + " 명"

        // Button
        dialogBinding.btnDialogConfirm.setOnClickListener { dialog.dismiss() }

        // 실행
        dialog.show()
    }

    // 투표 프로그래스바 계산
    fun calculateProgress(votes: Int, totalStudents: Int): Int {
        return if (totalStudents > 0) {
            (votes.toDouble() / totalStudents * 100).toInt() // 비율 계산 후 정수 변환
        } else {
            0 // 학생 수가 0이면 프로그래스 바는 0
        }
    }

    //
    private fun showVoteCreate() {
        if(nationViewModel.currentVoteInfo.value!!.voteStatus == VoteStatus.IN_PROGRESS){
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

    private fun cameraPermissionCheck() {
        showToast("클릭")

        permissionChecker = PermissionUtil(this)
        permissionChecker.setOnGrantedListener {
            // showToast("✅ 권한이 허용되었습니다.!")
            loadClassPhoto()
//            openGallery()
        }

        val cameraPermission = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)

        // 권한이 없는 경우에만 요청
        if (!permissionChecker.checkPermission(requireContext(), cameraPermission)) {
            permissionChecker.requestPermissionLauncher.launch(cameraPermission)
        }
    }

    private fun uploadImageToServer(imageUri: Uri) {
        lifecycleScope.launch {
            runCatching {
                // RetrofitUtil.authService.loginTeacher(TeacherSignInRequest(email = "aa@aa.com", password = "1234"))

                // Type? : MultipartBody.Part
            }.onSuccess {
                showToast("학급 사진 저장 완료")

            }.onFailure { error ->
                showToast("학급사진 저장 실패 ${error}")
            }
        }
    }

    private fun loadClassPhoto() {
//        pickImageLauncher?.launch("image/*")
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
}