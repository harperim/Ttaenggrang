package com.ladysparks.ttaenggrang

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.ladysparks.ttaenggrang.base.BaseActivity
import com.ladysparks.ttaenggrang.data.dummy.JobDummyData.jobList
import com.ladysparks.ttaenggrang.data.dummy.TaxDummyData.taxList
import com.ladysparks.ttaenggrang.data.model.dto.NationInfoDto
import com.ladysparks.ttaenggrang.data.remote.RetrofitUtil
import com.ladysparks.ttaenggrang.databinding.ActivityNationSetupBinding
import com.ladysparks.ttaenggrang.util.showErrorDialog
import com.ladysparks.ttaenggrang.util.showToast
import kotlinx.coroutines.launch

class NationSetupActivity : BaseActivity() {

    private val binding by lazy { ActivityNationSetupBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initEvent()
    }

    private fun initEvent(){
        binding.btnNationSetup.setOnClickListener {

            val name = binding.editNationName.text.toString()
            val population = binding.editNationPopulation.text.toString()
            val currency = binding.editCurrency.text.toString()
            val savingGoal = binding.editSavingGoal.text.toString()

            // 유효성 검사
            if(name.length == 0 || population.length == 0 || currency.length == 0 || savingGoal.length == 0){
                showToast("모든 항목값을 입력해주세요!")
                return@setOnClickListener
            }

            // LoginAPI 호출
            lifecycleScope.launch {
                runCatching {
                    val data = NationInfoDto(nationName = name, population = population.toInt(), currency = currency, savingsGoalAmount = savingGoal.toInt())
                    RetrofitUtil.authService.nationSetup(data)
                }.onSuccess {

//                    startActivity(Intent(this@NationSetupActivity, MainActivity::class.java))
                    // 1. 더미데이터 추가 : 직업 데이터
                    setBaseJobData()
                }.onFailure {
                    showErrorDialog(it)
                }
            }


        }
    }


    /**
     * 교사가 사용하기 위한 기본 데이터 추가 : 직업 + 세금
     */

    private fun setBaseJobData() {
        lifecycleScope.launch {
            runCatching {
                jobList.forEach { job ->
                    RetrofitUtil.teacherService.registerJob(job)
                }
            }.onSuccess {
                // 2. 더미데이터 추가 : 세금 데이터
                setBaseTaxData()
            }.onFailure {
                showErrorDialog(it)
            }
        }
    }

    private fun setBaseTaxData(){
        lifecycleScope.launch {
            runCatching {
                taxList.forEach { tax -> RetrofitUtil.taxService.registerTax(tax) }
            }.onSuccess {
                showToast("모든 데이터 생성이 끝났습니다.")
                startActivity(Intent(this@NationSetupActivity, LoginActivity::class.java))
            }.onFailure {
                showErrorDialog(it)
            }
        }
    }
}