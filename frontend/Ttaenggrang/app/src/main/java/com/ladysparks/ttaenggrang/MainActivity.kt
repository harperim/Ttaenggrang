package com.ladysparks.ttaenggrang

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import coil.decode.GifDecoder
import coil.load
import com.airbnb.lottie.LottieAnimationView
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

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setBackPress()

        // 0. 상, 하 네비게이션 영역 숨기기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        }

        // 필수 권한 확인
        permissionChecker = PermissionChecker(this@MainActivity, this)
        permissionChecker.requestPermissionsAtStartup()

        // 학생의 경우 FCM 메시지 수신 리스너
        if(!SharedPreferencesUtil.getValue(SharedPreferencesUtil.IS_TEACHER, false)){
            LocalBroadcastManager.getInstance(this)
                .registerReceiver(fcmReceiver, IntentFilter("FCM_MESSAGE_RECEIVED"))
        }

        // 초기설정
        initCategory() // 좌측 카테고리(메뉴) 설정
        initNavigationEvent() // 카테고리 클릭 이벤트 설정
        //observeLiveData()
        initEvent()
    }

    private fun setBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentCount = supportFragmentManager.backStackEntryCount

                if (fragmentCount > 1) {
                    supportFragmentManager.popBackStack()
                } else {
                    if (System.currentTimeMillis() - backPressedTime < 2000) {
                        finishAffinity()
                    } else {
                        backPressedTime = System.currentTimeMillis()
                        showToast("뒤로 버튼을 한 번 더 누르면 종료됩니다.")
                    }
                }
            }
        })
    }

    private val fcmReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val title = it.getStringExtra("title") ?: "알림"
                val message = it.getStringExtra("content") ?: "새로운 알림이 도착했습니다."
                val category = it.getStringExtra("category") ?: "일반"

                // 다이얼로그 표시
                showNotificationDialog(title, message, category)
            }
        }
    }

    private fun showNotificationDialog(title: String, message: String, category: String) {
        val dialog = Dialog(this)

        // 다이얼로그 레이아웃 설정 (XML 적용)
        dialog.setContentView(R.layout.dialog_alarm_detail)

        // 다이얼로그 내부 View 찾기
        val image: ImageView = dialog.findViewById(R.id.imgDialogStatus)
        val contentText: TextView = dialog.findViewById(R.id.textDialogTitle)
        val senderText: TextView = dialog.findViewById(R.id.textDialogContent)
        val btnConfirm: TextView = dialog.findViewById(R.id.btnDialogConfirm)

        // 카테고리별 GIF 적용
        loadCategoryImage(image, category)

        // 학생/선생 Type 별 페이지 이동 적용
        setClickEvent(
            dialog,
            btnConfirm,
            category,
            SharedPreferencesUtil.getValue(SharedPreferencesUtil.IS_TEACHER, false)
        )

        // 내용 표시
        contentText.text = title
        senderText.text = message

        dialog.show()


    }


    // FCM 이벤트 수신 관련 함수
    private fun setClickEvent(dialog: Dialog, btnConfirm: TextView, category: String, is_teacher: Boolean?) {
        btnConfirm.setOnClickListener {
            val targetPage = when {
                // BANK
                is_teacher == true && category == "BANK" -> {
                    btnConfirm.text = "이동"
                    NavigationManager.moveFragment(FRAGMENT_BANK_TEACHER)
                    dialog.dismiss()
                }

                is_teacher == false && category == "BANK" -> {
                    btnConfirm.text = "이동"
                    NavigationManager.moveFragment(FRAGMENT_BANK_STUDENT)
                    dialog.dismiss()
                }

                // NEWS
                is_teacher == true && category == "NEWS" -> {
                    btnConfirm.text = "이동"
                    NavigationManager.moveFragment(FRAGMENT_STOCK_TEACHER)
                    dialog.dismiss()
                }

                is_teacher == false && category == "NEWS" -> {
                    btnConfirm.text = "이동"
                    NavigationManager.moveFragment(FRAGMENT_STOCK_STUDENT)
                    dialog.dismiss()
                }

                else -> null
            }

            if (targetPage == null) {
                dialog.dismiss()
            }

        }
    }

    private fun loadCategoryImage(image: ImageView, category: String) {
        val imageRes = when (category) {
            "BANK" -> R.raw.dollar
            "REPORT" -> R.raw.report
            "NEWS" -> R.raw.news
            else -> R.raw.dollar
        }

        image.load(Uri.parse("android.resource://${this.packageName}/$imageRes")) {
            crossfade(true)
            decoderFactory(GifDecoder.Factory())
        }
    }


    // 좌측 Navigation(== Category 설정)
    private fun initCategory() {
        // 1. 카테고리 등록
        NavigationManager.register(this, binding.navigationView)

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
        val headerEmailTextView = headerView.findViewById<TextView>(R.id.navHeaderEmail)
        headerEmailTextView.text =
            SharedPreferencesUtil.getValue(SharedPreferencesUtil.USER_ACCOUNT, "정보없음")

        // 타입 구분
        val headerUserRole = headerView.findViewById<TextView>(R.id.navHeaderUserRole)
        headerUserRole.text = if (isTeacher) "교사" else "학생"
    }

    private fun initNavigationEvent() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            when (menuItem.itemId) {
                // Common
                R.id.navCountryInfo -> NavigationManager.moveFragment(FRAGMENT_NATION)

                // Teacher
                R.id.navHomeTeacher -> NavigationManager.moveFragment(FRAGMENT_HOME_TEACHER)
                R.id.navStudentsTeacher -> NavigationManager.moveFragment(FRAGMENT_STUDENT_MANAGEMENT)

                R.id.navStudentsJob -> NavigationManager.moveFragment(FRAGMENT_JOB)
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

    // Logout 설정
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
                    showTokenExpiredDialog()
                }
            }
        }
    }

    private fun showTokenExpiredDialog() {
        AlertDialog.Builder(this)
            .setTitle("토큰 만료")
            .setMessage("토큰이 만료되었습니다. 동시에 다른 사용자가 접속한 것은 아닌지 확인해주세요.")
            .setCancelable(false) // 다이얼로그 닫기 방지
            .setPositiveButton("확인") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .show()
    }

    companion object {
        const val CHANNEL_ID = "ttaenggrang_fcm_channel" // ✅ 채널 ID 정의
    }

}