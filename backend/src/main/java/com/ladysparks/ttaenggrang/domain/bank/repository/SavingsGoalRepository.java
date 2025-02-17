package com.ladysparks.ttaenggrang.domain.bank.repository;

import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    List<SavingsGoal> findByStudentId(Long studentId);

    boolean existsByStudentIdAndName(Long studentId, String name);

}
