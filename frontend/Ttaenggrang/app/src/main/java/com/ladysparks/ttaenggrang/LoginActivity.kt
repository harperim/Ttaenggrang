package com.ladysparks.ttaenggrang

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.ladysparks.ttaenggrang.base.BaseActivity
import com.ladysparks.ttaenggrang.data.model.dto.NationInfoDto
import com.ladysparks.ttaenggrang.data.model.request.StudentSignInRequest
import com.ladysparks.ttaenggrang.data.model.request.TeacherSignInRequest
import com.ladysparks.ttaenggrang.data.model.response.StudentSignInResponse
import com.ladysparks.ttaenggrang.data.model.response.TeacherSignInResponse
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.ActivityLoginBinding
import com.ladysparks.ttaenggrang.util.PermissionChecker
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import com.ladysparks.ttaenggrang.util.showErrorDialog
import com.ladysparks.ttaenggrang.util.showToast
import kotlinx.coroutines.launch
import android.provider.Settings


class LoginActivity : BaseActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private var isPasswordVisible = false
    private lateinit var fcmToken: String

    private lateinit var permissionChecker: PermissionChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 0. 인터넷 연결상태 확인
        if (!isInternetAvailable(this)) {
            showInternetDialog(this)
        }

        // 1. FCM Token 발급
        updateFCMToken()

        // 2. 필요 권한 요청
        permissionChecker = PermissionChecker(this@LoginActivity, this)
        permissionChecker.requestPermissionsAtStartup()

        // 3. 이벤트 정의
        tempEvent()
        initEvent()
    }

    private fun tempEvent() {
        binding.tempBtnTeacher.setOnClickListener {
            lifecycleScope.launch {
                runCatching {
                   // RetrofitUtil.authService.loginTeacher(TeacherSignInRequest(email = "abc@abc.com", password = "1234", fcmToken = fcmToken))
                    RetrofitUtil.authService.loginTeacher(TeacherSignInRequest(email = "hi1@naver.com", password = "ssafy123", fcmToken = fcmToken))
//                    RetrofitUtil.authService.loginTeacher(TeacherSignInRequest(email = "aa@aa.com", password = "1234"))
                }.onSuccess {
                    showToast("교사 로그인 성공")

                    // Token 저장
                    val token = when(val userData = it.data){
                        is TeacherSignInResponse -> userData.token
                        else -> ""
                    }
                    SharedPreferencesUtil.putValue(SharedPreferencesUtil.JWT_TOKEN_KEY, token)
                    SharedPreferencesUtil.putValue(SharedPreferencesUtil.IS_TEACHER, true)
                    SharedPreferencesUtil.putValue(SharedPreferencesUtil.USER_ACCOUNT, it.data!!.email)

                    // MainActivity 이동
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java)).apply {
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        finish()
                    }
                }.onFailure { error ->
                    showErrorDialog(error)
                }
            }
        }

        binding.tempBtnStudent.setOnClickListener {
            lifecycleScope.launch {
                runCatching {
//                    RetrofitUtil.authService.loginStudent(StudentSignInRequest(username = "aa002", password = "1234"))
                    RetrofitUtil.authService.loginStudent(StudentSignInRequest(username = "hello1", password = "ssafy123", fcmToken = fcmToken))
                   // RetrofitUtil.authService.loginStudent(StudentSignInRequest(username = "abc22", password = "1234", fcmToken = fcmToken))
                }.onSuccess {
                    showToast("학생 로그인 성공")

                    // Token 저장
                    val token = when(val userData = it.data){
                        is StudentSignInResponse -> userData.token
                        else -> ""
                    }
                    val userId = when (val userData = it.data) {
                        is StudentSignInResponse -> userData.id
                        else -> -1
                    }
                    SharedPreferencesUtil.putValue(SharedPreferencesUtil.JWT_TOKEN_KEY, token)
                    SharedPreferencesUtil.putValue(SharedPreferencesUtil.IS_TEACHER, false)
                    SharedPreferencesUtil.putValue(SharedPreferencesUtil.USER_ACCOUNT, it.data!!.username)
                    SharedPreferencesUtil.putValue(SharedPreferencesUtil.USER_ID, userId)

                    // MainActivity 이동
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java)).apply {
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        finish()
                    }
                }.onFailure { error ->
                    showErrorDialog(error)
                }
            }
        }
    }

    private fun initEvent() {
        // 회원 가입 페이지 이동
        binding.textLoginSignup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
        }

        // 로그인
        binding.btnLogin.setOnClickListener {
            // 이메일 형식 로그인일 경우 교사 : 상단 체크박스를 안누른 경우 대비 (시스템에서 대신 활성화 시켜줌)
            if(binding.editIdLogin.text.toString().contains("@")){
                binding.checkBoxAgree.isChecked = true
            }

            // 수정 필요 : 현재 하드코딩으로 체크박스 유무에 따라 학생, 선생님 로그인 다름
            lifecycleScope.launch {
                runCatching {
                    if (binding.checkBoxAgree.isChecked) {
                        var user = TeacherSignInRequest(email = binding.editIdLogin.text.toString(), password = binding.editPasswordLogin.text.toString(), fcmToken = fcmToken)
                        RetrofitUtil.authService.loginTeacher(user)
                    } else {
                        var user = StudentSignInRequest(username = binding.editIdLogin.text.toString(), password = binding.editPasswordLogin.text.toString(), fcmToken = fcmToken)
                        RetrofitUtil.authService.loginStudent(user)
                    }
                }.onSuccess {
//                     Token 저장
                    var token: String = ""
                    var account: String = ""
                    var hasNation: Boolean = false

                    when(val userData = it.data){
                        is StudentSignInResponse -> {
                            token = userData.token
                            account = userData.username
                        }
                        is TeacherSignInResponse -> {
                            token = userData.token
                            account = userData.email
                            hasNation = userData.hasNation
                        }
                        else -> ""
                    }

                    SharedPreferencesUtil.putValue(SharedPreferencesUtil.JWT_TOKEN_KEY, token)
                    SharedPreferencesUtil.putValue(SharedPreferencesUtil.IS_TEACHER, binding.checkBoxAgree.isChecked)
                    SharedPreferencesUtil.putValue(SharedPreferencesUtil.USER_ACCOUNT, account)

                    // 등록된 국가정보가 없을 경우, 다른 페이지로 이동
                    if(!hasNation && it.data is TeacherSignInResponse){
                        startActivity(Intent(this@LoginActivity, NationSetupActivity::class.java)).apply {
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            finish()
                        }
                        return@launch
                    }else{
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java)).apply {
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            finish()
                        }
                    }
                }.onFailure { error ->
                    Log.d("TAG", "initEvent: 로그인 패일")
                    showErrorDialog(error)
                }
            }
        }

        // 로그인 할 때 : 비밀번호 표시.숨김 토글
        binding.btnInvisiblePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                binding.editPasswordLogin.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.btnInvisiblePassword.setImageResource(R.drawable.ic_visible)
            } else {
                binding.editPasswordLogin.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.btnInvisiblePassword.setImageResource(R.drawable.ic_invisible)
            }
            //커서 위치 유지
            binding.editPasswordLogin.setSelection(binding.editPasswordLogin.text.length)
        }
    }

    // FCM Token 정보를 업데이트 합니다.
    private fun updateFCMToken() {
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if(!task.isSuccessful)  return@OnCompleteListener
            if(task.result != null){
                fcmToken = task.result
                Log.d(" FCM TOKEN !", "getFCMToken: ${task.result}")
            }
        })
    }

    // 인터넷 연결 확인 함수
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showInternetDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("인터넷 연결 필요")
            .setMessage("인터넷이 연결되지 않았어요. Wi-Fi나 데이터를 켜주세요!")
            .setPositiveButton("설정으로 이동") { _, _ ->
                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
            .setNegativeButton("닫기") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false) // 뒤로 가기 버튼 방지
            .show()
    }
}