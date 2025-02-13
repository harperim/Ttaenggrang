package com.ladysparks.ttaenggrang.domain.teacher.service;

import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.dto.JobClassDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.JobCreateDTO;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Job;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import com.ladysparks.ttaenggrang.domain.teacher.repository.JobRespository;
import com.ladysparks.ttaenggrang.domain.student.repository.StudentRepository;
import com.ladysparks.ttaenggrang.domain.teacher.repository.TeacherRepository;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRespository jobRespository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentService studentService;

    public ApiResponse<JobCreateDTO> createJob(JobCreateDTO jobCreateDTO, Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("교사를 찾을 수 없습니다."));

        // 1. 직업 중복 체크
        Optional<Job> existingJob = jobRespository.findByJobName(jobCreateDTO.getJobName());
        if (existingJob.isPresent()) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "직업 이름이 이미 존재합니다.", null);
        }

        // 2. 직업 엔터티 생성
        Job job = Job.builder()
                .jobName(jobCreateDTO.getJobName())
                .jobDescription(jobCreateDTO.getJobDescription())
                .baseSalary(jobCreateDTO.getBaseSalary())
                .maxPeople(jobCreateDTO.getMaxPeople())
                .teacher(teacher)
                .salaryDate(new Timestamp(System.currentTimeMillis()))
                .build();

        // 3. 학생 ID가 있을 경우, 학생 조회 및 직업 연결
        if (jobCreateDTO.getStudentIds() != null && !jobCreateDTO.getStudentIds().isEmpty()) {
            List<Student> students = studentRepository.findAllById(jobCreateDTO.getStudentIds());

            // 조회된 학생이 없는 경우
            if (students.isEmpty()) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "해당 학생들을 찾을 수 없습니다.", null);
            }

            // 학생들을 직업에 연결
            for (Student student : students) {
                student.setJob(job);
            }

            job.setStudents(students);
        }

        // 4. 직업 저장
        Job savedJob = jobRespository.save(job);

        List<Long> studentIds = savedJob.getStudents() != null
                ? savedJob.getStudents().stream().map(Student::getId).toList()
                : Collections.emptyList();

        // 5. 응답 DTO 생성
        JobCreateDTO responseDTO = new JobCreateDTO(
                savedJob.getId(),
                savedJob.getJobName(),
                savedJob.getJobDescription(),
                savedJob.getBaseSalary(),
                savedJob.getMaxPeople(),
                studentIds
        );

        return ApiResponse.created("직업 등록이 완료되었습니다.", responseDTO);
    }

    // 우리 반 직업 정보 조회
    @Transactional(readOnly = true)
    public ApiResponse<List<JobClassDTO>> getClassJobs(Long teacherId) {
        // 1. 교사 id로 우리 반 직업 조회
        List<Job> jobs = jobRespository.findAllByTeacherId(teacherId);

        if (jobs.isEmpty()) {
            return ApiResponse.error(HttpStatus.NOT_FOUND.value(), "우리 반 직업이 없습니다.", null);
        }

        // 2. Job 엔터티를 JobClassDTO로 변환
        List<JobClassDTO> jobClassDTOS = jobs.stream()
                .map(job -> JobClassDTO.builder()
                        .id(job.getId())
                        .jobName(job.getJobName())
                        .jobDescription(job.getJobDescription())
                        .baseSalary(job.getBaseSalary())
                        .maxPeople(job.getMaxPeople())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.success("우리 반 직업 목록 조회 성공", jobClassDTOS);
    }

    public int findBaseSalaryByStudentId(Long studentId) {
        Long jobId = studentService.findJobIdByStudentId(studentId);
        return jobRespository.findBaseSalaryById(jobId);
    }
}
