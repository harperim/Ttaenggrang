package com.ladysparks.ttaenggrang.domain.bank.repository;

import com.ladysparks.ttaenggrang.domain.bank.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT COALESCE(AVG(b.balance), 0) FROM BankAccount b WHERE b.id IN " +
            "(SELECT s.bankAccount.id FROM Student s WHERE s.teacher.id = :teacherId)")
    double getAverageBalanceByTeacherId(@Param("teacherId") Long teacherId);

}
