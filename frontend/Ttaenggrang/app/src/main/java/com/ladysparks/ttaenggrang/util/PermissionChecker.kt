package com.ladysparks.ttaenggrang.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class PermissionChecker(private val context: Context, activityOrFragment: Any) {

    private var shouldShowSettingsDialog = false // 설정 이동 다이얼로그 띄울지 여부
    private val REQUIRED_PERMISSIONS = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            android.Manifest.permission.POST_NOTIFICATIONS, // 알림 권한 (Android 13+)
            android.Manifest.permission.INTERNET,          // 인터넷 사용
            android.Manifest.permission.READ_MEDIA_IMAGES  // 갤러리 (Android 13+)
        )
    } else {
        arrayOf(
            android.Manifest.permission.INTERNET,              // 인터넷 사용
            android.Manifest.permission.READ_EXTERNAL_STORAGE  // 갤러리 (Android 12 이하)
        )
    }

    // 권한 체크 후 결과 반환
    fun checkPermissions(permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 앱 실행 시 자동으로 권한 요청
    fun requestPermissionsAtStartup() {
        if (!checkPermissions(REQUIRED_PERMISSIONS)) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
        when (activityOrFragment) {
            is AppCompatActivity -> activityOrFragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { resultChecking(it) }

            is Fragment -> activityOrFragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { resultChecking(it) }

            else -> throw RuntimeException("Activity 혹은 Fragment에서만 사용할 수 있습니다.")
        }

    private fun resultChecking(result: Map<String, Boolean>) {
        Log.d("TAG", "requestPermissionLauncher: 요청된 권한 개수 : ${result.size}")

        if (result.values.contains(false)) { // ✅ 하나라도 거부됨
            if (shouldShowSettingsDialog) {
                // 두 번째 거부 → 앱 강제 종료
                Toast.makeText(context, "필수 권한이 없어 앱을 종료합니다.", Toast.LENGTH_SHORT).show()
                (context as? AppCompatActivity)?.finishAffinity()
            } else {
                // 첫 번째 거부 → 설정 화면으로 이동 다이얼로그 표시
                shouldShowSettingsDialog = true
                showSettingsDialog()
            }
        } else {
            Toast.makeText(context, "모든 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 권한 거부 시 설정 이동 다이얼로그 표시
    private fun showSettingsDialog() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("권한 필요")
        alertDialog.setMessage("이 앱을 사용하려면 필수 권한이 필요합니다. 설정에서 권한을 허용해주세요.")
        alertDialog.setPositiveButton("설정으로 이동") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:" + context.packageName))
            context.startActivity(intent)
        }
        alertDialog.setNegativeButton("앱 종료") { _, _ ->
            (context as? AppCompatActivity)?.finishAffinity() // 앱 종료
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}
