package com.ladysparks.ttaenggrang.ui.nation

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.ladysparks.ttaenggrang.R
import com.ladysparks.ttaenggrang.base.BaseFragment
import com.ladysparks.ttaenggrang.databinding.DialogNationRegisterBinding
import com.ladysparks.ttaenggrang.ui.component.BaseTwoButtonDialog
import com.ladysparks.ttaenggrang.databinding.DialogVoteCreateBinding
import com.ladysparks.ttaenggrang.databinding.DialogVoteParticipationBinding
import com.ladysparks.ttaenggrang.databinding.DialogVoteStatusBinding
import com.ladysparks.ttaenggrang.databinding.FragmentNationBinding
import com.ladysparks.ttaenggrang.util.PermissionUtil
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import com.ladysparks.ttaenggrang.util.showToast
import kotlinx.coroutines.launch

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

        initSetting()
        initData()
        initEvent()
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
            binding.btnNationInfo.visibility = View.GONE
            binding.btnGoalSavings.visibility = View.GONE
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

    private fun initData() {
        lifecycleScope.launch {
            runCatching {
                // 국가 정보. 목표 자산, 투표현황 등의 내용을가져옴
                // RetrofitUtil.authService.loginTeacher(TeacherSignInRequest(email = "aa@aa.com", password = "1234"))
            }.onSuccess {
                showToast("학급 정보 불러오기 API 추가해야함")
                // 학급 이미지 처리 로직
                if(true){
                    // 학급 사진이 있는 경우
                }else{
                    // 학급 사진이 없는 경우
                    binding.imgClassPhoto.visibility = View.GONE
                    binding.textClassPhotoNull.visibility= View.VISIBLE
                }

            }.onFailure { error ->
                showToast("학급정보 불러오기 실패 ${error}")
            }
        }
    }

    private fun initEvent() {
        binding.constraintClassPhoto.setOnClickListener{ loadClassPhoto() }
        binding.textVoteNow.setOnClickListener { showVoteNow() }  // 학생이 '투표하러 가기' 눌렀을 때
        binding.textVoteStatus.setOnClickListener { showVoteTeacher() } // 선생님이 '투표 생성/관리' 눌렀을 때
        binding.btnNationInfo.setOnClickListener { createNationIfoDialog() } // 국가 정보 설정 눌렀을 대
        binding.btnGoalSavings.setOnClickListener { createGoalDialog() } // 목표자산 설정 눌렀을 때
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
            onPositiveClick = { showVoteCreate() },
            onNegativeClick = { showVoteStatus() },
            onCloseClick = {}
        )
        dialog.show()
    }

    private fun showVoteStatus() {
        val dialogBinding = DialogVoteStatusBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        // Info
        dialogBinding.textVoteTitle.text = "우리반 봉사왕"
        dialogBinding.textVoteDate.text = "1998.02.16 ~ 2025.02.10"
        dialogBinding.textVoteTotal.text = "31명"

        // 1등
        dialogBinding.textRankFirstName.text = "서미지"
        dialogBinding.tvVoteCountFirst.text = "12명"
        dialogBinding.progressBarFirst.progress = 70

        // 2등
        dialogBinding.textRankSecondName.text = "김미지"
        dialogBinding.tvVoteCountSecond.text = "8명"
        dialogBinding.progressBarSecond.progress = 40

        // 3등
        dialogBinding.textRankThirdName.text = "박미지"
        dialogBinding.tvVoteCountThird.text = "1명"
        dialogBinding.progressBarThird.progress = 20


        // Button
        dialogBinding.btnDialogConfirm.setOnClickListener { dialog.dismiss() }


        // 실행
        dialog.show()
    }

    private fun showVoteCreate() {
        if(false){
            // 투표 생성 불가 : 이미 진행 중인 투표가 있음
            val dialog = BaseTwoButtonDialog(
                context = requireContext(),
                title = "진행중인 투표가 존재합니다!",
                message = "현재 진행주인 투표가 있습니다\n투표를 종료하고 새 투표를 생성하시겠습니까?",
                positiveButtonText = "취소",
                negativeButtonText = "투표 종료",
                statusImageResId = R.drawable.ic_warning,
                showCloseButton = true,
                onPositiveClick = { showToast("취소") },
                onNegativeClick = { showToast("종료시키고 새롭게...") },
                onCloseClick = {}
            )
            dialog.show()
        }else{
            val dialogBinding = DialogVoteCreateBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

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

            // Button
            dialogBinding.btDialogCancel.setOnClickListener { dialog.dismiss() }
            dialogBinding.btnDialogConfirm.setOnClickListener {
                showToast("투표가 생성되었습니다")
                dialog.dismiss()
            }

            // Start
            dialog.show()

        }

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
        // 수정 : 국가 설립일, 단위, 학급인원 추가 되어야 함
        // 학급이원이 변경된 경우 수정 요청 api 날려야함. 후순위

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