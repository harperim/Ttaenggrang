package com.ladysparks.ttaenggrang

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.ladysparks.ttaenggrang.base.BaseActivity
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.ActivityMainBinding
import com.ladysparks.ttaenggrang.ui.bank.BankStudentFragment
import com.ladysparks.ttaenggrang.ui.bank.BankTeacherFragment
import com.ladysparks.ttaenggrang.ui.home.HomeStudentFragment
import com.ladysparks.ttaenggrang.ui.home.HomeTeacherFragment
import com.ladysparks.ttaenggrang.ui.job.JobFragment
import com.ladysparks.ttaenggrang.ui.nation.NationFragment
import com.ladysparks.ttaenggrang.ui.revenue.RevenueStudentFragment
import com.ladysparks.ttaenggrang.ui.revenue.RevenueTeacherFragment
import com.ladysparks.ttaenggrang.ui.stock.StockStudentFragment
import com.ladysparks.ttaenggrang.ui.stock.StockTeacherFragment
import com.ladysparks.ttaenggrang.ui.store.StoreStudentFragment
import com.ladysparks.ttaenggrang.ui.store.StoreTeacherFragment
import com.ladysparks.ttaenggrang.ui.students.StudentsFragment
import com.ladysparks.ttaenggrang.util.SharedPreferencesUtil
import com.ladysparks.ttaenggrang.util.showToast
import kotlinx.coroutines.launch


class MainActivity : BaseActivity() {
    companion object {
        const val CHANNEL_ID = "ttaenggrang_fcm_channel" // ✅ 채널 ID 정의
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }

        initData()
        initEvent()

//         학생, 교사 구분해서 카테고리 변경
        val isTeacher = SharedPreferencesUtil.getValue(SharedPreferencesUtil.IS_TEACHER, false)
        if (isTeacher) {
            binding.navigationView.menu.clear()
            binding.navigationView.inflateMenu(R.menu.nav_menu_teacher)
            binding.navigationView.setCheckedItem(R.id.navHomeTeacher)
            replaceFragment(HomeTeacherFragment())
        } else {
            binding.navigationView.menu.clear()
            binding.navigationView.inflateMenu(R.menu.nav_menu_student)
            binding.navigationView.setCheckedItem(R.id.navHomeStudent)
            replaceFragment(HomeStudentFragment())
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true  // 선택한 아이템 활성화
            when (menuItem.itemId) {
                // Common
                R.id.navCountryInfo -> replaceFragment(NationFragment())

                // Teacher
                R.id.navHomeTeacher -> replaceFragment(HomeTeacherFragment())
                R.id.navStudentsTeacher -> replaceFragment(StudentsFragment())
                R.id.navStudentsJob -> replaceFragment(JobFragment())
                R.id.navRevenueTeacher -> replaceFragment(RevenueTeacherFragment())
                R.id.navBankTeacher -> replaceFragment(BankTeacherFragment())
                R.id.navStockTeacher -> replaceFragment(StockTeacherFragment())
                R.id.navShopTeacher -> replaceFragment(StoreTeacherFragment())

                // Student
                R.id.navHomeStudent -> replaceFragment(HomeStudentFragment())
                R.id.navRevenueStudent -> replaceFragment(RevenueStudentFragment())
                R.id.navBankStudent -> replaceFragment(BankStudentFragment())
                R.id.navStockStudent -> replaceFragment(StockStudentFragment())
                R.id.navShopStudent  -> replaceFragment(StoreStudentFragment())
            }
            true
        }
    }


    private fun initData() {
        val headerView = binding.navigationView.getHeaderView(0)

        // Email + Name
        val headerEmailTextView = headerView.findViewById<TextView>(R.id.navHeaderEmail)
        headerEmailTextView.text = SharedPreferencesUtil.getValue(SharedPreferencesUtil.USER_ACCOUNT, "정보없음")

        // 타입 구분
        val headerUserRole = headerView.findViewById<TextView>(R.id.navHeaderUserRole)
        val isTeacher = SharedPreferencesUtil.getValue(SharedPreferencesUtil.IS_TEACHER, false)
        headerUserRole.text = if (isTeacher) "교사" else "학생"
    }

    // Fragment 이동을 위한 함수
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun initEvent() {
        binding.btnSettings.setOnClickListener {
            lifecycleScope.launch {
                runCatching {
                    if (SharedPreferencesUtil.getValue(SharedPreferencesUtil.IS_TEACHER, false)){
                        RetrofitUtil.authService.logoutTeacher()
                    }else{
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
}