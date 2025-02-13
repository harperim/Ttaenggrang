package com.ladysparks.ttaenggrang.domain.student.repository;

import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.teacher.dto.StudentManagementDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // 메서드 네이밍만 사용하면 Student 객체 전체가 반환 -> @Query 사용
    // 특정 학생(Student ID)의 BankAccount ID 조회 (JPQL 사용)
    @Query("SELECT s.bankAccount.id FROM Student s WHERE s.id = :studentId")
    Long findBankAccountIdById(@Param("studentId") Long studentId);

    // username으로 학생 계정 조회하는 메서드
    Optional<Student> findByUsername(String username);

//    // 특정 직업을 가진 학생 목록 조회
//    List<Student> findByJobId(Long jobId);

    // 교사의 ID를 기준으로 학생 목록 조회 (우리반 학생 전체 조회)
    List<Student> findAllByTeacherId(Long teacherId);

    // 교사의 ID와 학생 ID를 기준으로 특정 학생 조회 (우리반 특정 학생 조회)
    Optional<Student> findByIdAndTeacherId(Long studentId, Long teacherId);

    // 교사ID 와 직업ID로 학생 목록 조회 (특정 직업을 가진 학생 목록 조회)
    List<Student> findByTeacherIdAndJobId(Long teacherId, Long jobId);

    Optional<Student> findNationIdById(Long studentId);;

    int countByTeacherId(long teacherId);

    @Query("SELECT s.job.id FROM Student s WHERE s.id = :studentId")
    Long findJobIdById(Long studentId);

    @Query("SELECT new com.ladysparks.ttaenggrang.domain.teacher.dto.StudentManagementDTO( " +
            "s.id, s.name, s.username, " +
            "COALESCE(j.jobName, ''), COALESCE(j.baseSalary, 0), COALESCE(b.balance, 0)) " +
            "FROM Student s " +
            "LEFT JOIN s.job j " +
            "LEFT JOIN s.bankAccount b " +
            "WHERE s.teacher.id = :teacherId")
    List<StudentManagementDTO> getStudentManagementListByTeacherId(@Param("teacherId") Long teacherId);

}
