package com.ladysparks.ttaenggrang.domain.vote.service;

import com.ladysparks.ttaenggrang.domain.student.dto.StudentResponseDTO;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
import com.ladysparks.ttaenggrang.domain.teacher.dto.JobInfoDTO;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import com.ladysparks.ttaenggrang.domain.teacher.repository.TeacherRepository;
import com.ladysparks.ttaenggrang.domain.vote.dto.RankInfoDTO;
import com.ladysparks.ttaenggrang.domain.vote.dto.VoteCreateDTO;
import com.ladysparks.ttaenggrang.domain.vote.dto.VoteItemResponseDTO;
import com.ladysparks.ttaenggrang.domain.vote.entity.*;
import com.ladysparks.ttaenggrang.domain.vote.repository.VoteHistoryRepository;
import com.ladysparks.ttaenggrang.domain.vote.repository.VoteItemRepository;
import com.ladysparks.ttaenggrang.domain.vote.repository.VoteRepository;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import com.ladysparks.ttaenggrang.global.utill.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final StudentRepository studentRepository;
    private final VoteItemRepository voteItemRepository;
    private final TeacherRepository teacherRepository;
    private final VoteHistoryRepository voteHistoryRepository;


    // ✅ 이메일로 교사 ID 조회 메서드
    public Long getTeacherIdByEmail(String email) {
        return teacherRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 교사를 찾을 수 없습니다."))
                .getId();
    }

    // ✅ 현재 로그인한 사용자가 교사인지 학생인지 구분하여 teacherId 가져오기
    private Long getClassTeacherIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.");
        }

        Object principalObj = authentication.getPrincipal();
        if (principalObj instanceof UserDetails) {
            String username = ((UserDetails) principalObj).getUsername();

            // ✅ 먼저 교사인지 확인
            Optional<Teacher> teacher = teacherRepository.findByEmail(username);
            if (teacher.isPresent()) {
                Long teacherId = teacher.get().getId();
//                System.out.println("✅ 로그인한 사용자가 교사입니다. teacherId: " + teacherId);
                return teacherId;
            }

            // ✅ 교사가 아니라면 학생인지 확인
            Optional<Student> student = studentRepository.findByUsername(username);
            if (student.isPresent()) {
                Long classTeacherId = student.get().getTeacher().getId();  // 🔥 학생이 속한 교사의 ID 가져오기
//                System.out.println("✅ 로그인한 사용자가 학생입니다. 해당 반의 teacherId: " + classTeacherId);
                return classTeacherId;
            }

            // ✅ 학생도 교사도 아닐 경우 예외 발생
            throw new IllegalArgumentException("해당 username을 가진 교사 또는 학생을 찾을 수 없습니다.");
        }

        throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
    }


    // 투표 [생성]
    @Transactional
    public ApiResponse<VoteCreateDTO> createVote(VoteCreateDTO voteCreateDTO, Long teacherId) {

        // 1. 진행 중인 투표 확인
        Optional<Vote> inProgressVote = voteRepository.findByStatus(VoteStatus.IN_PROGRESS);
        if (inProgressVote.isPresent()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "진행 중인 투표가 있어 새 투표를 생성할 수 없습니다.", null);
        }

        // 투표 종료 시간 확인 로그
        System.out.println("투표 생성 시작 - 제목: " + voteCreateDTO.getTitle());
        System.out.println("시작 시간: " + voteCreateDTO.getStartDate());
        System.out.println("종료 시간: " + voteCreateDTO.getEndDate());

        // 2. 새 투표 생성
        Vote vote = Vote.builder()
                .title(voteCreateDTO.getTitle())
                .startDate(voteCreateDTO.getStartDate())
                .endDate(voteCreateDTO.getEndDate())
                .voteMode(voteCreateDTO.getVoteMode())
                .status(VoteStatus.IN_PROGRESS)
                .teacher(teacherRepository.findById(teacherId)
                        .orElseThrow(() -> new IllegalArgumentException("교사를 찾을 수 없습니다.")))
                .build();

        voteRepository.save(vote);

        // ✅ 3. 학생 리스트를 저장할 변수 선언 (초기값 null)
        List<StudentResponseDTO> studentResponseList = null;

        // 4. 투표 모드가 STUDENT인 경우, 학생 리스트를 투표 항목으로 추가
        if (voteCreateDTO.getVoteMode() == VoteMode.STUDENT) {
            List<Student> studentList = studentRepository.findAllByTeacherId(teacherId);
            if (studentList.isEmpty()) {
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "우리 반 학생이 없습니다.", null);
            }

            // 학생 리스트를 투표 항목으로 저장
            studentList.forEach(student -> {
                VoteItem voteItem = VoteItem.builder()
                        .vote(vote)
                        .student(student)
                        .voteCount(0)  // 초기 투표 수는 0
                        .build();
                voteItemRepository.save(voteItem);
            });

            // ✅ 학생 리스트를 DTO로 변환하고 변수에 저장
            studentResponseList = studentList.stream()
                    .map(student -> {
                        JobInfoDTO jobInfo = null;
                        if (student.getJob() != null) {
                            jobInfo = JobInfoDTO.builder()
                                    .jobName(student.getJob().getJobName())
                                    .baseSalary(student.getJob().getBaseSalary())
                                    .build();
                        }

                        // StudentResponseDTO
                        return new StudentResponseDTO(
                                student.getId(),
                                student.getUsername(),
                                student.getName(),
                                student.getProfileImageUrl(),
                                student.getTeacher(),
                                student.getBankAccount(),
                                jobInfo,
                                null  // 토큰 값은 필요 시 추가
                        );
                    })
                    .toList();
            }

            // 5. 투표 정보 + 학생 리스트를 DTO로 변환해서 반환
            VoteCreateDTO response = new VoteCreateDTO();
            response.setTitle(vote.getTitle());
            response.setStartDate(vote.getStartDate());
            response.setEndDate(vote.getEndDate());
            response.setVoteMode(vote.getVoteMode());
            response.setVoteStatus(vote.getStatus());
//            response.setStudents(studentResponseList);  // ✅ 학생 리스트 포함 (null일 수도 있음)

            return ApiResponse.success("새 투표가 성공적으로 생성되었습니다!", response);
        }

    // ✅ 진행 중인 투표 조회 메서드
    public ApiResponse<VoteCreateDTO> getCurrentVote() {
        // 현재 로그인한 교사의 반 교사 ID 가져오기 (교사면 본인, 학생이면 반의 교사)
        Long teacherId = getClassTeacherIdFromSecurityContext();

        Optional<Vote> currentVoteOpt = voteRepository.findByTeacherIdAndStatus(teacherId, VoteStatus.IN_PROGRESS);

        // 진행 중인 투표가 없는 경우, 완료된 마지막 투표 결과 조회
        if (currentVoteOpt.isEmpty()) {
            Optional<Vote> lastCompletedVote = voteRepository.findTopByStatusOrderByEndDateDesc(VoteStatus.COMPLETED);
            if (lastCompletedVote.isEmpty()) {
                return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "진행 중인 투표가 없습니다.", null);
            }

            Vote completedVote = lastCompletedVote.get();
            VoteCreateDTO response = buildVoteResult(completedVote);
            return ApiResponse.success("완료된 투표 결과 조회 성공", response);
        }

        // 진행 중인 투표가 있는 경우
        Vote currentVote = currentVoteOpt.get();

        // 자동으로 투표 종료 시간 체크 후 상태 업데이트
        if (currentVote.getEndDate().before(new Timestamp(System.currentTimeMillis()))) {
            currentVote.setStatus(VoteStatus.COMPLETED);
            voteRepository.save(currentVote);
            VoteCreateDTO response = buildVoteResult(currentVote);
            return ApiResponse.success("투표가 종료되었으며 결과를 반환합니다.", response);
        }

        // 진행 중인 투표 정보 반환
        VoteCreateDTO response = buildVoteResult(currentVote);
        return ApiResponse.success("진행 중인 투표 정보 조회 성공", response);
    }

    // 투표 결과를 DTO로 변환하는 메서드
    private VoteCreateDTO buildVoteResult(Vote vote) {
        List<VoteItem> voteItems = voteItemRepository.findByVoteId(vote.getId());

        // 상위 3명 정렬
        List<VoteItem> top3Items = voteItems.stream()
                .sorted((v1, v2) -> Integer.compare(v2.getVoteCount(), v1.getVoteCount()))
                .limit(3)
                .toList();

        // 전체 인원 및 투표 참여 인원 계산
        int totalStudents = studentRepository.countByTeacherId(vote.getTeacher().getId());
        int totalVotes = voteItems.stream().mapToInt(VoteItem::getVoteCount).sum();

        // DTO로 변환
        VoteCreateDTO voteResultDTO = new VoteCreateDTO();
        voteResultDTO.setId(vote.getId());
        voteResultDTO.setTitle(vote.getTitle());
        voteResultDTO.setStartDate(vote.getStartDate());
        voteResultDTO.setEndDate(vote.getEndDate());
        voteResultDTO.setVoteMode(vote.getVoteMode());
        voteResultDTO.setVoteStatus(vote.getStatus());
        voteResultDTO.setTotalStudents(totalStudents);
        voteResultDTO.setTotalVotes(totalVotes);

        // 상위 3명의 학생 정보 추가
        for (int i = 0; i < top3Items.size(); i++) {
            VoteItem item = top3Items.get(i);
            RankInfoDTO rankInfoDTO = new RankInfoDTO(i + 1, item.getVoteCount(), item.getStudent().getName());
            voteResultDTO.getTopRanks().add(rankInfoDTO);
        }

        return voteResultDTO;
    }

    // ✅ 진행 중인 투표 종료 메서드
    @Transactional
    public ApiResponse<String> stopCurrentVote() {
        Optional<Vote> currentVote = voteRepository.findByStatus(VoteStatus.IN_PROGRESS);

        if (currentVote.isEmpty()) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "진행 중인 투표가 없습니다.", null);
        }

        Vote vote = currentVote.get();
        vote.setStatus(VoteStatus.COMPLETED);  // ✅ 상태를 COMPLETED로 변경
        voteRepository.save(vote);

        return ApiResponse.success("진행 중인 투표가 성공적으로 종료되었습니다.");
    }

    // 투표 항목 조회 (우리 반 친구들 목록)
    public ApiResponse<List<VoteItemResponseDTO>> getCurrentVoteItems() {
        // 진행 중인 투표 조회
        Optional<Vote> currentVoteOpt = voteRepository.findByStatus(VoteStatus.IN_PROGRESS);

        if (currentVoteOpt.isEmpty()) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "진행 중인 투표가 없습니다.", null);
        }

        Vote currentVote = currentVoteOpt.get();

        // 투표 항목 조회
        List<VoteItem> voteItems = voteItemRepository.findByVoteId(currentVote.getId());

        if (voteItems.isEmpty()) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "투표 항목이 없습니다.", null);
        }

        // VoteItemResponseDTO로 변환
        List<VoteItemResponseDTO> response = voteItems.stream().map(item -> new VoteItemResponseDTO(
                item.getId(),
                item.getStudent().getId(),
                item.getStudent().getName(),
                item.getStudent().getProfileImageUrl(),
                item.getVoteCount()
        )).toList();

        return ApiResponse.success("투표 항목 조회 성공", response);
    }


    // 학생 투표 : 우리 반 친구 선택해서 제출하면, 해당 학생의 투표 수 증가
    @Transactional
    public ApiResponse<String> castStudentVote(Long voteItemId) {
        try {
//            System.out.println("투표 항목 ID: " + voteItemId);

            VoteItem voteItem = voteItemRepository.findById(voteItemId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 투표 항목을 찾을 수 없습니다."));

            System.out.println("투표 상태: " + voteItem.getVote().getStatus());
            System.out.println("투표 종료 시간: " + voteItem.getVote().getEndDate());
            System.out.println("현재 시간: " + new Timestamp(System.currentTimeMillis()));

            System.out.println("투표 항목 ID: " + voteItem.getId());
            System.out.println("연결된 투표 ID: " + voteItem.getVote().getId());


            if (voteItem.getVote().getStatus() != VoteStatus.IN_PROGRESS) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "투표가 종료되어 투표할 수 없습니다.", null);
            }

            Student student = getCurrentStudent();
//            System.out.println("현재 로그인한 학생 ID: " + student.getId());

            // **수정된 부분: 특정 투표(Vote)에 대한 중복 투표 여부 확인**
            boolean hasVoted = voteHistoryRepository.existsByStudent_IdAndVoteItem_Vote_Id(student.getId(), voteItem.getVote().getId());
            if (hasVoted) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "이미 투표하셨습니다.", null);
            }

            voteItem.setVoteCount(voteItem.getVoteCount() + 1);
            voteItemRepository.save(voteItem);

            VoteHistory voteHistory = VoteHistory.builder()
                    .student(student)
                    .voteItem(voteItem)
                    .build();
            voteHistoryRepository.save(voteHistory);

            return ApiResponse.success("투표가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();  // 예외 로그 출력
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류가 발생했습니다.", null);
        }
    }


    private Student getCurrentStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.");
        }

        Object principalObj = authentication.getPrincipal();
        if (principalObj instanceof UserDetails) {
            String username = ((UserDetails) principalObj).getUsername();

            // 사용자 이름(이메일 또는 학번)을 기반으로 학생 정보 조회
            return studentRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다."));
        }

        throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
    }



}


