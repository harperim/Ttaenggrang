package com.ladysparks.ttaenggrang

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_BANK_STUDENT
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_BANK_TEACHER
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_HOME_STUDENT
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_HOME_TEACHER
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_JOB
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_NATION
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_REVENUE_STUDENT
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_REVENUE_TEACHER
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_STOCK_STUDENT
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_STOCK_TEACHER
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_STORE_STUDENT
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_STORE_TEACHER
import com.ladysparks.ttaenggrang.util.NavigationManager.FRAGMENT_STUDENT_MANAGEMENT
import com.ladysparks.ttaenggrang.base.BaseActivity
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.ActivityMainBinding
import com.ladysparks.ttaenggrang.util.NavigationManager
import com.ladysparks.ttaenggrang.util.PermissionChecker
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import com.ladysparks.ttaenggrang.util.showToast
import kotlinx.coroutines.launch


class MainActivity : BaseActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var permissionChecker: PermissionChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 0. 상, 하 네비게이션 영역 숨기기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }

        // 필수 권한 확인
        permissionChecker = PermissionChecker(this@MainActivity, this)
        permissionChecker.requestPermissionsAtStartup()


        initCategory() // 좌측 카테고리(메뉴) 설정
        initNavigationEvent() // 카테고리 클릭 이벤트 설정
        //observeLiveData()
        initEvent()
    }



    private fun initCategory() {
        // 1. 카테고리 등록
        NavigationManager.register(this)

        // 2. 학생, 교사 로그인 상황에 따라 다른 메뉴 적용
        val isTeacher = SharedPreferencesUtil.getValue(SharedPreferencesUtil.IS_TEACHER, false)
        if (isTeacher) {
            binding.navigationView.menu.clear()
            binding.navigationView.inflateMenu(R.menu.nav_menu_teacher)
            binding.navigationView.setCheckedItem(R.id.navHomeTeacher)
            NavigationManager.moveFragment(FRAGMENT_HOME_TEACHER)
        } else {
            binding.navigationView.menu.clear()
            binding.navigationView.inflateMenu(R.menu.nav_menu_student)
            binding.navigationView.setCheckedItem(R.id.navHomeStudent)
            NavigationManager.moveFragment(FRAGMENT_HOME_STUDENT)
        }

        // 3. 카테고리 헤더 정보 등록
        val headerView = binding.navigationView.getHeaderView(0)

        // Email + Name
        val headerEmailTextView = headerView.findViewById<TextView>(R.id.navHeaderEmail)
        headerEmailTextView.text =
            SharedPreferencesUtil.getValue(SharedPreferencesUtil.USER_ACCOUNT, "정보없음")

        // 타입 구분
        val headerUserRole = headerView.findViewById<TextView>(R.id.navHeaderUserRole)
        headerUserRole.text = if (isTeacher) "교사" else "학생"
    }

    private fun initNavigationEvent() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true  // 선택한 아이템 활성화
            when (menuItem.itemId) {
                // Common
                R.id.navCountryInfo -> NavigationManager.moveFragment(FRAGMENT_NATION)

                // Teacher
                R.id.navHomeTeacher -> NavigationManager.moveFragment(FRAGMENT_HOME_TEACHER)
                R.id.navStudentsTeacher -> NavigationManager.moveFragment(FRAGMENT_STUDENT_MANAGEMENT)
                R.id.navStudentsJob -> NavigationManager. moveFragment(FRAGMENT_JOB)
                R.id.navRevenueTeacher -> NavigationManager.moveFragment(FRAGMENT_REVENUE_TEACHER)
                R.id.navBankTeacher -> NavigationManager.moveFragment(FRAGMENT_BANK_TEACHER)
                R.id.navStockTeacher -> NavigationManager.moveFragment(FRAGMENT_STOCK_TEACHER)
                R.id.navStoreTeacher -> NavigationManager.moveFragment(FRAGMENT_STORE_TEACHER)

                // Student
                R.id.navHomeStudent -> NavigationManager.moveFragment(FRAGMENT_HOME_STUDENT)
                R.id.navRevenueStudent -> NavigationManager.moveFragment(FRAGMENT_REVENUE_STUDENT)
                R.id.navBankStudent -> NavigationManager.moveFragment(FRAGMENT_BANK_STUDENT)
                R.id.navStockStudent -> NavigationManager.moveFragment(FRAGMENT_STOCK_STUDENT)
                R.id.navStoreStudent -> NavigationManager.moveFragment(FRAGMENT_STORE_STUDENT)
            }
            true
        }
    }



    private fun initEvent() {
        binding.btnSettings.setOnClickListener {
            lifecycleScope.launch {
                runCatching {
                    if (SharedPreferencesUtil.getValue(SharedPreferencesUtil.IS_TEACHER, false)) {
                        RetrofitUtil.authService.logoutTeacher()
                    } else {
                        RetrofitUtil.authService.logoutStudent()
                    }
                }.onSuccess {
                    // 토큰 & 교사/학생 구분 제거
                    SharedPreferencesUtil.removeValue(SharedPreferencesUtil.JWT_TOKEN_KEY)
                    SharedPreferencesUtil.removeValue(SharedPreferencesUtil.IS_TEACHER)

                    showToast("로그아웃 완료")
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java).apply {
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }.onFailure {
                    showToast("로그아웃 실패")
                }
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "ttaenggrang_fcm_channel" // ✅ 채널 ID 정의
    }

}