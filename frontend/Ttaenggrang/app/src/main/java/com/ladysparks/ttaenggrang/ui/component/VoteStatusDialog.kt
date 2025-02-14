package com.ladysparks.ttaenggrang.ui.component

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.ladysparks.ttaenggrang.databinding.DialogVoteStatusBinding
import com.ladysparks.ttaenggrang.data.model.dto.VoteDataDto
import com.ladysparks.ttaenggrang.data.model.dto.VoteStatus
import com.ladysparks.ttaenggrang.util.DataUtil

class VoteStatusDialog(private val context: Context, private val voteInfo: VoteDataDto) {

    fun show() {
        if (voteInfo.voteStatus == VoteStatus.COMPLETED) {
            Toast.makeText(context, "현재 진행중인 투표가 없습니다!", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogBinding = DialogVoteStatusBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .create()

        // 날짜 변환
        val startDate = DataUtil.formatDate(DataUtil.formatDateTimeFromServer(voteInfo.startDate)!!)
        val endDate = DataUtil.formatDate(DataUtil.formatDateTimeFromServer(voteInfo.endDate)!!)

        // Info
        dialogBinding.textVoteTitle.text = voteInfo.title
        dialogBinding.textVoteDate.text = "${startDate} ~ ${endDate}"
        dialogBinding.textVoteTotal.text = voteInfo.totalVotes.toString() + " 명"

        // 1등
        if (voteInfo.topRanks?.size ?: 0 > 0) {
            dialogBinding.textRankFirstName.text = voteInfo.topRanks!![0].studentName
            dialogBinding.tvVoteCountFirst.text = "${voteInfo.topRanks!![0].votes} 명"
            dialogBinding.progressBarFirst.progress = calculateProgress(voteInfo.topRanks!![0].votes, voteInfo.totalStudents!!)
        }

        // 2등
        if (voteInfo.topRanks?.size ?: 0 > 1) {
            dialogBinding.textRankSecondName.text = voteInfo.topRanks!![1].studentName
            dialogBinding.tvVoteCountSecond.text = "${voteInfo.topRanks!![1].votes} 명"
            dialogBinding.progressBarSecond.progress = calculateProgress(voteInfo.topRanks!![1].votes, voteInfo.totalStudents!!)
        }

        // 3등
        if (voteInfo.topRanks?.size ?: 0 > 2) {
            dialogBinding.textRankThirdName.text = voteInfo.topRanks!![2].studentName
            dialogBinding.tvVoteCountThird.text = "${voteInfo.topRanks!![2].votes} 명"
            dialogBinding.progressBarThird.progress = calculateProgress(voteInfo.topRanks!![2].votes, voteInfo.totalStudents!!)
        }

        // 버튼 이벤트
        dialogBinding.btnDialogConfirm.setOnClickListener { dialog.dismiss() }

        // 다이얼로그 실행
        dialog.show()
    }

    private fun calculateProgress(votes: Int, totalStudents: Int): Int {
        return if (totalStudents > 0) (votes * 100) / totalStudents else 0
    }
}