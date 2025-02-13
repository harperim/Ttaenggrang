package com.ladysparks.ttaenggrang.domain.vote.repository;

import com.ladysparks.ttaenggrang.domain.vote.entity.Vote;
import com.ladysparks.ttaenggrang.domain.vote.entity.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {
    // ✅ 투표 결과를 투표 수 기준 내림차순으로 가져오기
    List<VoteItem> findByVoteOrderByVoteCountDesc(Vote vote);

    List<VoteItem> findByVoteId(Long voteId);
}
