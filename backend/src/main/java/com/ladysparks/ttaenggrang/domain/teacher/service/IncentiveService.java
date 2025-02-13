package com.ladysparks.ttaenggrang.domain.teacher.service;

import com.ladysparks.ttaenggrang.domain.bank.entity.BankAccount;
import com.ladysparks.ttaenggrang.domain.bank.repository.BankAccountRepository;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
import com.ladysparks.ttaenggrang.domain.teacher.dto.IncentiveDTO;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Incentive;
import com.ladysparks.ttaenggrang.domain.teacher.repository.IncentiveRepository;
import com.ladysparks.ttaenggrang.domain.teacher.repository.TeacherRepository;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IncentiveService {

    private final StudentRepository studentRepository;
    private final BankAccountRepository bankAccountRepository;
    private final IncentiveRepository incentiveRepository;

    @Transactional
    public ApiResponse<String> giveIncentive(IncentiveDTO incentiveDTO) {
        // 1. 학생 정보 조회
        Student student = studentRepository.findById(incentiveDTO.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다."));

        // 2. 학생 계좌 확인
        BankAccount account = student.getBankAccount();
        if (account == null) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "학생의 계좌 정보가 없습니다.", null);
        }

        // 3. 최근 인센티브 지급 기록 확인
        Optional<Incentive> recentIncentiveOpt = incentiveRepository.findTopByStudentIdOrderByCreatedAtDesc(student.getId());
        if (recentIncentiveOpt.isPresent()) {
            Incentive recentIncentive = recentIncentiveOpt.get();
            Timestamp oneWeekAgo = new Timestamp(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000);  // 7일 전

            if (recentIncentive.getCreatedAt().after(oneWeekAgo)) {
                return ApiResponse.error((HttpStatus.BAD_REQUEST.value()), "최근 일주일 이내에 이미 인센티브가 지급되었습니다.", null);
            }
        }

        // 4. 인센티브 지급
        account.setBalance(account.getBalance() + incentiveDTO.getIncentive());
        bankAccountRepository.save(account);

        // 5. 인센티브 기록 저장
        Incentive incentive = Incentive.builder()
                .student(student)
                .incentive(incentiveDTO.getIncentive())
                .build();

        incentiveRepository.save(incentive);

        return ApiResponse.success("인센티브가 성공적으로 지급되었습니다.");
    }

}
