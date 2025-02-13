package com.ladysparks.ttaenggrang.domain.teacher.service;

import com.ladysparks.ttaenggrang.domain.teacher.dto.TeacherLoginDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.TeacherResponseDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.TeacherSignupDTO;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Nation;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import com.ladysparks.ttaenggrang.domain.teacher.repository.NationRepository;
import com.ladysparks.ttaenggrang.domain.teacher.repository.TeacherRepository;
import com.ladysparks.ttaenggrang.global.config.JwtTokenProvider;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import com.ladysparks.ttaenggrang.global.utill.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;  // ✅ JWT 토큰 생성기
    private final SecurityUtil securityUtil;
    private final NationRepository NationRepository;

    public Long getCurrentTeacherId() {
        String email = securityUtil.getCurrentUser();
        return teacherRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 교사를 찾을 수 없습니다."))
                .getId();
    }

    public Optional<Long> getOptionalCurrentTeacherId() {
        String email = securityUtil.getCurrentUser();
        return teacherRepository.findByEmail(email).map(Teacher::getId);
    }

    // 회원가입
    public TeacherSignupDTO signupTeacher(TeacherSignupDTO teacherSignupDTO) {
        // 중복 이메일 체크
        // 이미 해당 이메일을 가진 Teacher가 존재하는지 확인 후 저장하기
        if (teacherRepository.findByEmail(teacherSignupDTO.getEmail()).isPresent()) {
            throw new IllegalIdentifierException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 일치 여부 확인
        if (!teacherSignupDTO.getPassword1().equals(teacherSignupDTO.getPassword2())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Teacher teacher = Teacher.builder()
                .email(teacherSignupDTO.getEmail())
                .name(teacherSignupDTO.getName())
                .school(teacherSignupDTO.getSchool())
                .password(passwordEncoder.encode(teacherSignupDTO.getPassword2()))  // 비밀번호 암호화
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        teacherRepository.save(teacher);

        return TeacherSignupDTO.builder()
                .id(teacher.getId())
                .email(teacher.getEmail())
                .name(teacher.getName())
                .school(teacher.getSchool())
                .createdAt(teacher.getCreatedAt())
                .build();
    }

    // 로그인
    public TeacherLoginDTO loginTeacher(TeacherLoginDTO teacherLoginDTO) {
        Teacher teacher = teacherRepository.findByEmail(teacherLoginDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(teacherLoginDTO.getPassword(), teacher.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JMT 토큰 생성
        String token = jwtTokenProvider.createToken(teacher.getEmail());

        // 교사가 국가 정보를 가지고 있는지 확인
        boolean hasNation = teacher.getNation() != null;

        // 응답을 위한 DTO 생성 (요청용 DTO와 구별)
        return new TeacherLoginDTO(teacher.getEmail(), teacher.getName(), teacher.getSchool(), hasNation, token);
    }

    // 교사 전체 목록 조회 (조회용)
    public ApiResponse<List<TeacherResponseDTO>> getAllTeachers() {
        List<Teacher> teachers = teacherRepository.findAll();

        if (teachers.isEmpty()) {
            return ApiResponse.error(404, "등록된 교사가 없습니다.", null);
        }

        List<TeacherResponseDTO> responseDTOs = teachers.stream()
                .map(teacher -> new TeacherResponseDTO(
                        teacher.getId(),
                        teacher.getEmail(),
                        teacher.getName(),
                        teacher.getSchool(),
                        teacher.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ApiResponse.success("교사 목록 조회 성공", responseDTOs);
    }

    public List<TeacherResponseDTO> findAllTeachers() {
        List<Teacher> teachers = teacherRepository.findAll();

        if (teachers.isEmpty()) {
            throw new NotFoundException("등록된 교사가 없습니다.");
        }

        List<TeacherResponseDTO> responseDTOs = teachers.stream()
                .map(teacher -> new TeacherResponseDTO(
                        teacher.getId(),
                        teacher.getEmail(),
                        teacher.getName(),
                        teacher.getSchool(),
                        teacher.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return responseDTOs;
    }

    public String findNameById(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .map(Teacher::getName)
                .orElseThrow(() -> new NotFoundException("등록된 교사가 없습니다."));
    }

}
