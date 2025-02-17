package com.ladysparks.ttaenggrang.domain.bank.repository;

import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SavingsProductRepository extends JpaRepository<SavingsProduct, Long> {

    List<SavingsProduct> findByTeacherId(Long teacherId);

    boolean existsByTeacherIdAndName(Long teacherId, String name);

    @Query("SELECT s.durationWeeks FROM SavingsProduct s WHERE s.id = :savingsProductId")
    Optional<Long> findDurationWeeksById(@Param("savingsProductId") Long savingsProductId);

    /**
     * subscriberCount를 1 증가
     */
    @Modifying
    @Query("UPDATE SavingsProduct s SET s.subscriberCount = s.subscriberCount + 1 " +
            "WHERE s.id = :savingsProductId")
    int incrementSubscriberCount(@Param("savingsProductId") Long savingsProductId);

    /**
     * subscriberCount를 1 감소
     */
    @Modifying
    @Query("UPDATE SavingsProduct s SET s.subscriberCount = s.subscriberCount - 1 " +
            "WHERE s.id = :savingsProductId AND s.subscriberCount > 0")
    int decrementSubscriberCount(@Param("savingsProductId") Long savingsProductId);

    /**
     * 특정 교사가 등록한 적금 상품 개수
     */
    @Query("SELECT COUNT(s) FROM SavingsProduct s WHERE s.teacher.id = :teacherId")
    long countSavingsProductsByTeacherId(@Param("teacherId") Long teacherId);

}
