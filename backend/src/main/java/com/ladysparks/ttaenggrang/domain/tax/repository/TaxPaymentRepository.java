package com.ladysparks.ttaenggrang.domain.tax.repository;

import com.ladysparks.ttaenggrang.domain.tax.dto.OverdueTaxPaymentDTO;
import com.ladysparks.ttaenggrang.domain.tax.dto.TeacherStudentTaxPaymentDTO;
import com.ladysparks.ttaenggrang.domain.tax.entity.TaxPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaxPaymentRepository extends JpaRepository<TaxPayment, Long> {

    @Query("SELECT t FROM TaxPayment t WHERE t.student.id = :studentId")
    List<TaxPayment> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT t FROM TaxPayment t WHERE t.tax.id = :taxId")
    List<TaxPayment> findByTaxId(@Param("taxId") Long taxId);

    @Query("SELECT t FROM TaxPayment t WHERE t.student.teacher.id = :teacherId")
    List<TaxPayment> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT t FROM TaxPayment t WHERE t.student.teacher.id = :teacherId AND t.id = :taxId")
    List<TaxPayment> findByTeacherIdAndTaxId(@Param("teacherId") Long teacherId, @Param("taxId") Long taxId);

    List<TaxPayment> findByStudentIdAndPaymentDateBetweenOrderByPaymentDateDesc(Long studentId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT new com.ladysparks.ttaenggrang.domain.tax.dto.TeacherStudentTaxPaymentDTO(" +
            "s.id, s.name, COALESCE(SUM(t.amount), 0)) " +
            "FROM Student s " +
            "LEFT JOIN TaxPayment t ON s.id = t.student.id " +
            "WHERE s.teacher.id = :teacherId " +
            "GROUP BY s.id, s.name " +
            "ORDER BY s.name ASC")
    List<TeacherStudentTaxPaymentDTO> findStudentTaxPaymentsByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT new com.ladysparks.ttaenggrang.domain.tax.dto.TeacherStudentTaxPaymentDTO(" +
            "s.id, s.name, COALESCE(SUM(t.amount), 0)) " +
            "FROM Student s " +
            "LEFT JOIN TaxPayment t ON s.id = t.student.id " +
            "WHERE s.id = :studentId " +
            "GROUP BY s.id, s.name")
    TeacherStudentTaxPaymentDTO findStudentTaxPaymentByStudentId(Long studentId);


    @Query(value = "SELECT '벌금' AS description, " +
            "GROUP_CONCAT(tx.tax_name SEPARATOR ', ') AS taxNames, " +
            "COALESCE(SUM(t.amount), 0) AS totalAmount " +
            "FROM tax_payment t " +
            "JOIN tax tx ON t.tax_id = tx.id " +
            "WHERE t.is_overdue = true AND t.student_id = :studentId",
            nativeQuery = true)
    OverdueTaxPaymentDTO.OverdueTaxPaymentProjection findOverdueTaxPaymentByStudentId(@Param("studentId") Long studentId);

    @Modifying // 업데이트 쿼리
    @Query("UPDATE TaxPayment t SET t.isOverdue = false " +
            "WHERE t.isOverdue = true AND t.student.id = :studentId")
    int updateOverdueToPaidByStudentId(@Param("studentId") Long studentId);

}
