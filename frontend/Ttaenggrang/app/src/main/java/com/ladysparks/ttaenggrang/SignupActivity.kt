package com.ladysparks.ttaenggrang

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.ladysparks.ttaenggrang.base.BaseActivity
import com.ladysparks.ttaenggrang.data.dummy.JobDummyData.jobList
import com.ladysparks.ttaenggrang.data.dummy.TaxDummyData.taxList
import com.ladysparks.ttaenggrang.data.model.request.TeacherSignUpRequest
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.ActivitySignupBinding
import com.ladysparks.ttaenggrang.util.showErrorDialog
import com.ladysparks.ttaenggrang.util.showToast
import kotlinx.coroutines.launch
import java.util.Date

class SignupActivity : BaseActivity() {

    // binding
    private val binding by lazy { ActivitySignupBinding.inflate(layoutInflater) }

    // 비밀번호 입력, 확인 란의 현재 표시/숨김 상태
    private var isPasswordVisible = false
    private var isPasswordCheckVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // btnSignup
        binding.btnSignup.setOnClickListener {
            signUp()
        }

        // 비밀번호 표시.숨김 토글
        binding.btnInvisiblePasswordSignup.setOnClickListener{
            isPasswordVisible = !isPasswordVisible

            if(isPasswordVisible) {
                binding.editPasswordSignup.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.btnInvisiblePasswordSignup.setImageResource(R.drawable.ic_visible)
            } else {
                binding.editPasswordSignup.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.btnInvisiblePasswordSignup.setImageResource(R.drawable.ic_invisible)
            }
            //커서 위치 유지
            binding.editPasswordSignup.setSelection(binding.editPasswordSignup.text.length)
        }

        // 비밀번호 확인 표시/숨김 토글
        binding.btnInvisiblePasswordcheckSignup.setOnClickListener{
            isPasswordCheckVisible = !isPasswordCheckVisible

            if(isPasswordCheckVisible) {
                binding.editPasswordcheckSignup.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.btnInvisiblePasswordcheckSignup.setImageResource(R.drawable.ic_visible)
            } else {
                binding.editPasswordcheckSignup.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.btnInvisiblePasswordcheckSignup.setImageResource(R.drawable.ic_invisible)
            }
            //커서 위치 유지
            binding.editPasswordcheckSignup.setSelection(binding.editPasswordcheckSignup.text.length)
        }

    }



    private fun signUp() {
        // 1. input Data가져오기
        val email = binding.editEmailSignup.text.toString().trim()
        val password1 = binding.editPasswordSignup.text.toString().trim()
        val password2 = binding.editPasswordcheckSignup.text.toString().trim()
        val name = binding.editNameSignup.text.toString().trim()
        val school = binding.editSchoolSignup.text.toString().trim()
        val createdAt = Date().time

        // 1-2. 모든 필드가 작성되었는지 확인
        if (email.isEmpty() || name.isEmpty() || school.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
             showToast("모든 정보를 작성해주세요")
            return
        }

        // 1-3. 비밀번호와 비밀번호 확인이 일치하는지 확인
        if (password1 != password2) {
             showToast("비밀번호가 일치하지 않습니다")
            return
        }

        // 2. TeacherSignUpRequest 객체 생성
        val user = TeacherSignUpRequest(0, email, password1, password2, name, school, createdAt)

        lifecycleScope.launch {
            runCatching {
                // 서버로부터 알림 데이터 요청
                RetrofitUtil.authService.signupTeacher(user)
            }.onSuccess { data ->
                showToast("회원 가입 완료 ! ${data}")
                startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            }.onFailure { exception ->
                showErrorDialog(exception)
            }
        }
    }


}
