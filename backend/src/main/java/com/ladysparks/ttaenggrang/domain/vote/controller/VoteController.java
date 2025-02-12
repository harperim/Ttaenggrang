package com.ladysparks.ttaenggrang.domain.vote.controller;

import com.ladysparks.ttaenggrang.domain.vote.dto.VoteCreateDTO;
import com.ladysparks.ttaenggrang.domain.vote.entity.Vote;
import com.ladysparks.ttaenggrang.domain.vote.entity.VoteItem;
import com.ladysparks.ttaenggrang.domain.vote.entity.VoteItemResponseDTO;
import com.ladysparks.ttaenggrang.domain.vote.service.VoteService;
import com.ladysparks.ttaenggrang.global.docs.VoteApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/votes")
@RequiredArgsConstructor
public class VoteController implements VoteApiSpecification {

    private final VoteService voteService;

    // 현재 로그인한 교사의 ID 가져오기
    private Long getTeacherIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.");
        }

        Object principalObj = authentication.getPrincipal();
        if (principalObj instanceof UserDetails) {
            String email = ((UserDetails) principalObj).getUsername();
            return voteService.getTeacherIdByEmail(email);
        }
        throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
    }

    // 새 투표 [생성]
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<VoteCreateDTO>> createVote(@RequestBody @Valid VoteCreateDTO voteCreateDTO) {
        Long teacherId = getTeacherIdFromSecurityContext();
        ApiResponse<VoteCreateDTO> response = voteService.createVote(voteCreateDTO, teacherId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // ✅ 현재 진행 중인 투표 [조회]
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<VoteCreateDTO>> getCurrentVote() {
        ApiResponse<VoteCreateDTO> response = voteService.getCurrentVote();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // ✅ 현재 진행 중인 투표 [종료]
    @PostMapping("/stop")
    public ResponseEntity<ApiResponse<String>> stopCurrentVote() {
        ApiResponse<String> response = voteService.stopCurrentVote();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // 투표 항목 조회 (우리 반 아이들)
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<VoteItemResponseDTO>>> getVoteItems() {
        ApiResponse<List<VoteItemResponseDTO>> response = voteService.getCurrentVoteItems();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // 학생 투표
    @PostMapping("/student")
    public ResponseEntity<ApiResponse<String>> castStudentVote(@RequestParam Long voteItemId) {
        ApiResponse<String> response = voteService.castStudentVote(voteItemId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
