package com.ladysparks.ttaenggrang.domain.bank.repository;

import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {

    List<BankTransaction> findByBankAccountId(Long bankAccountId);

    @Query("SELECT t FROM BankTransaction t WHERE t.bankAccount.id = :bankAccountId AND t.createdAt BETWEEN :startDate AND :endDate")
    List<BankTransaction> findTransactionsByBankAccountAndDateRange(
            @Param("bankAccountId") Long bankAccountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM BankTransaction t WHERE t.bankAccount.id IN :bankAccountIds AND t.createdAt BETWEEN :startDate AND :endDate")
    List<BankTransaction> findTransactionsByBankAccountsAndDateRange(
            @Param("bankAccountIds") List<Long> bankAccountIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM BankTransaction t WHERE t.bankAccount.id = (SELECT s.bankAccount.id FROM Student s WHERE s.id = :studentId) " +
            "AND t.createdAt BETWEEN :startDate AND :endDate")
    List<BankTransaction> findTransactionsByStudentIdAndDateRange(
            @Param("studentId") Long studentId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM BankTransaction t " +
            "WHERE t.bankAccount.id IN (SELECT s.bankAccount.id FROM Student s WHERE s.id = :studentId) " +
            "AND t.createdAt BETWEEN :startDate AND :endDate " +
            "AND t.type IN (:transactionTypes)")
    int getTotalAmountByType(@Param("studentId") Long studentId,
                             @Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate,
                             @Param("transactionTypes") List<BankTransactionType> transactionTypes);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM BankTransaction t " +
            "WHERE t.bankAccount.id IN (SELECT s.bankAccount.id FROM Student s WHERE s.id = :studentId) " +
            "AND t.type IN (:transactionTypes)")
    int getTotalAmountByType(@Param("studentId") Long studentId,
                             @Param("transactionTypes") List<BankTransactionType> transactionTypes);

}

