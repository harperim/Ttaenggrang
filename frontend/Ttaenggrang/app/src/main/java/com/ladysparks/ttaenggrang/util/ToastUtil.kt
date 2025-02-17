package com.ladysparks.ttaenggrang.util

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

// Activity & Fragment 공통으로 Toast 사용 가능
fun Context?.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    this?.let { Toast.makeText(it, message, duration).show() }
}

// Fragment에서도 편하게 사용 가능
fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().showToast(message, duration)
}