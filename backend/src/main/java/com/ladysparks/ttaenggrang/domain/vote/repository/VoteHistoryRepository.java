package com.ladysparks.ttaenggrang.domain.vote.repository;

import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.vote.entity.VoteHistory;
import com.ladysparks.ttaenggrang.domain.vote.entity.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VoteHistoryRepository extends JpaRepository<VoteHistory, Long> {

    Optional<VoteHistory> findByStudent_IdAndVoteItem_Id(long studentId, long voteItemId);

    // 특정 학생이 특정 투표에서 이미 투표했는지 확인
    boolean existsByStudent_IdAndVoteItem_Vote_Id(long studentId, long voteId);
}
