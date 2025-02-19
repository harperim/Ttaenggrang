package com.ladysparks.ttaenggrang.util

import android.util.Log
import com.google.android.material.navigation.NavigationView
import com.ladysparks.ttaenggrang.MainActivity
import com.ladysparks.ttaenggrang.R
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

/**
 * Fragment 에서 이동 할때 사용 예시 (Fragment 변경 + Category 변경)
 * NavigationManager.moveFragment(NavigationManager.FRAGMENT_BANK_TEACHER)
 */
object NavigationManager {
    // common
    val FRAGMENT_NATION = 14

    // TEATURE
    val FRAGMENT_HOME_TEACHER = 1
    val FRAGMENT_STUDENT_MANAGEMENT = 15
    val FRAGMENT_JOB = 2
    //val FRAGMENT_NATION_TEACHER = 3
    val FRAGMENT_REVENUE_TEACHER = 4
    val FRAGMENT_BANK_TEACHER = 5
    val FRAGMENT_STOCK_TEACHER = 6
    val FRAGMENT_STORE_TEACHER = 7


    // STUDENT
    val FRAGMENT_HOME_STUDENT = 8
    // val FRAGMENT_NATION_STUDENT = 9
    val FRAGMENT_REVENUE_STUDENT = 10
    val FRAGMENT_BANK_STUDENT = 11
    val FRAGMENT_STOCK_STUDENT = 12
    val FRAGMENT_STORE_STUDENT = 13

    // Fragment 이동 관련
    private var mainActivity: MainActivity? = null
    private var navigationView: NavigationView? = null

    fun register(activity: MainActivity, navView: NavigationView) {
        mainActivity = activity
        navigationView = navView
    }

    fun moveFragment(value: Int) {
        mainActivity?.let {
            val transaction = it.supportFragmentManager.beginTransaction()
            when(value) {
                FRAGMENT_NATION -> transaction.replace(R.id.fragment_container, NationFragment())

                FRAGMENT_HOME_TEACHER -> transaction.replace(R.id.fragment_container, HomeTeacherFragment())
                FRAGMENT_STUDENT_MANAGEMENT -> transaction.replace(R.id.fragment_container, StudentsFragment())
                FRAGMENT_JOB -> transaction.replace(R.id.fragment_container, JobFragment())
                FRAGMENT_REVENUE_TEACHER -> transaction.replace(R.id.fragment_container, RevenueTeacherFragment())
                FRAGMENT_BANK_TEACHER -> transaction.replace(R.id.fragment_container, BankTeacherFragment())
                FRAGMENT_STOCK_TEACHER -> transaction.replace(R.id.fragment_container, StockTeacherFragment())
                FRAGMENT_STORE_TEACHER -> transaction.replace(R.id.fragment_container, StoreTeacherFragment())

                FRAGMENT_HOME_STUDENT -> transaction.replace(R.id.fragment_container, HomeStudentFragment())
                FRAGMENT_REVENUE_STUDENT -> transaction.replace(R.id.fragment_container, RevenueStudentFragment())
                FRAGMENT_BANK_STUDENT -> transaction.replace(R.id.fragment_container, BankStudentFragment())
                FRAGMENT_STOCK_STUDENT -> transaction.replace(R.id.fragment_container, StockStudentFragment())
                FRAGMENT_STORE_STUDENT -> transaction.replace(R.id.fragment_container, StoreStudentFragment())

                else -> return
            }
            transaction.commit()

            // ㅌ프래그먼트 이동 후 NavigationView 선택 상태 변경
            setCheckedNavigationItem(value)
        }
    }

    private fun setCheckedNavigationItem(value: Int) {
        Log.d("TAG", "setCheckedNavigationItem: 맞음 ${value}")
        val itemId = when (value) {
            FRAGMENT_NATION -> R.id.navCountryInfo

            FRAGMENT_HOME_TEACHER -> R.id.navHomeTeacher
            FRAGMENT_STUDENT_MANAGEMENT -> R.id.navStudentsTeacher
            FRAGMENT_JOB -> R.id.navStudentsJob
            FRAGMENT_REVENUE_TEACHER -> R.id.navRevenueTeacher
            FRAGMENT_BANK_TEACHER -> R.id.navBankTeacher
            FRAGMENT_STOCK_TEACHER -> R.id.navStockTeacher
            FRAGMENT_STORE_TEACHER -> R.id.navStoreTeacher

            FRAGMENT_HOME_STUDENT -> R.id.navHomeStudent
            FRAGMENT_REVENUE_STUDENT -> R.id.navRevenueStudent
            FRAGMENT_BANK_STUDENT -> R.id.navBankStudent
            FRAGMENT_STOCK_STUDENT -> R.id.navStockStudent
            FRAGMENT_STORE_STUDENT -> R.id.navStoreStudent

            else -> return
        }

        //  NavigationView의 선택 상태 변경
        navigationView?.setCheckedItem(itemId)
    }

}