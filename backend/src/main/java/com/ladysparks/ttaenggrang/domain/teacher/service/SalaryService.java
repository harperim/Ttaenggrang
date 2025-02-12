package com.ladysparks.ttaenggrang.domain.teacher.service;

import com.ladysparks.ttaenggrang.domain.bank.entity.BankAccount;
import com.ladysparks.ttaenggrang.domain.bank.repository.BankAccountRepository;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
import com.ladysparks.ttaenggrang.domain.teacher.entity.SalaryHistory;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import com.ladysparks.ttaenggrang.domain.teacher.repository.SalaryHistoryRepository;
import com.ladysparks.ttaenggrang.domain.teacher.repository.TeacherRepository;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final StudentRepository studentRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TeacherRepository teacherRepository;
    private final SalaryHistoryRepository salaryHistoryRepository;

    // 현재 로그인한 교사의 ID 가져오기
    private Long getTeacherIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.");
        }

        Object principalObj = authentication.getPrincipal();
        if (principalObj instanceof UserDetails) {
            String email = ((UserDetails) principalObj).getUsername();  // 이메일 가져오기
            return teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 교사를 찾을 수 없습니다."))
                    .getId();
        }

        throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
    }


    // 주급 부여
    @Transactional
    public ApiResponse<String> distributeBaseSalary() {

        // 1. 현재 로그인한 교사 ID 가져오기
        Long teacherId = getTeacherIdFromSecurityContext();

        // 2. 최근 급여 지급 기록 확인
        Optional<SalaryHistory> lastSalaryOpt = salaryHistoryRepository.findTopByTeacherIdOrderByDistributedAtDesc(teacherId);
        if (lastSalaryOpt.isPresent()) {
            Timestamp lastDistributedAt = lastSalaryOpt.get().getDistributedAt();
            LocalDateTime onWeekAgo = LocalDateTime.now().minusWeeks(1);

            // 최급 지급일이 7일 이내라면 지급 불가
            if (lastDistributedAt.toLocalDateTime().isAfter(onWeekAgo)) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "이미 이번 주 주급이 지급되었습니다.", null);
            }
        }

        // 3. 교사 유효성 검증
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 교사를 찾을 수 없습니다."));

        // 4. 교사 반 학생들 조회
        List<Student> students = studentRepository.findAllByTeacherId(teacherId);

        if (students.isEmpty()) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "우리 반 학생이 없습니다.", null);
        }

        // 5. 학생 별로 기본급 지급
        for (Student student : students) {
            if (student.getJob() != null && student.getBankAccount() != null) {
                int baseSalary = student.getJob().getBaseSalary();
                BankAccount account = student.getBankAccount();

                // 계좌 잔액 업데이트
                account.setBalance(account.getBalance() + baseSalary);
                bankAccountRepository.save(account);
            }
        }

        // 6. 급여 지급 기록 저장
        SalaryHistory salaryHistory = SalaryHistory.builder()
                .teacher(teacher)
                .distributedAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        salaryHistoryRepository.save(salaryHistory);

        return ApiResponse.success("주급이 성공적으로 지급되었습니다.");
    }

}
