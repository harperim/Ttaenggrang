package com.ladysparks.ttaenggrang.domain.tax.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.BankAccountDTO;
import com.ladysparks.ttaenggrang.domain.bank.dto.BankTransactionDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsDeposit;
import com.ladysparks.ttaenggrang.domain.bank.service.BankAccountService;
import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
import com.ladysparks.ttaenggrang.domain.tax.dto.OverdueTaxPaymentDTO;
import com.ladysparks.ttaenggrang.domain.tax.dto.TeacherStudentTaxPaymentDTO;
import com.ladysparks.ttaenggrang.domain.teacher.dto.NationDTO;
import com.ladysparks.ttaenggrang.domain.teacher.service.NationService;
import com.ladysparks.ttaenggrang.domain.tax.dto.TaxDTO;
import com.ladysparks.ttaenggrang.domain.tax.dto.TaxPaymentDTO;
import com.ladysparks.ttaenggrang.domain.tax.entity.TaxPayment;
import com.ladysparks.ttaenggrang.domain.tax.mapper.TaxPaymentMapper;
import com.ladysparks.ttaenggrang.domain.tax.repository.TaxPaymentRepository;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaxPaymentService {

    private final TaxPaymentRepository taxPaymentRepository;
    private final TaxPaymentMapper taxPaymentMapper;

    private final NationService nationService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final BankAccountService bankAccountService;
    private final BankTransactionService bankTransactionService;
    private final TaxService taxService;

    // 세금 납부 등록 & 국고 합산
    @Transactional
    public TaxPaymentDTO addTaxPayment(TaxPaymentDTO taxPaymentDTO) {
        Long studentId = studentService.getCurrentStudentId();

        // 국가 정보 조회
        Long nationId = studentService.getNationIdById(studentId);

        // 은행 계좌 거래
        Long bankAccountId = studentService.findBankAccountIdById(studentId);
        BankAccountDTO bankAccountDTO = bankAccountService.findBankAccount(bankAccountId);

        TaxDTO taxDTO = taxService.findTaxById(taxPaymentDTO.getTaxId());

        BankTransactionDTO bankTransactionDTO = BankTransactionDTO.builder()
                .bankAccountId(bankAccountDTO.getId())
                .type(BankTransactionType.TAX)
                .amount(taxPaymentDTO.getAmount())
                .description("[세금 납부] 세금명: " + taxDTO.getTaxName())
                .receiverId(bankAccountDTO.getId())
                .build();
        bankTransactionService.addBankTransaction(bankTransactionDTO);

        // 세금 납부 금액을 국가의 국고에 합산
        NationDTO updatedNationDTO = nationService.updateNationFunding(nationId, taxPaymentDTO.getAmount());

        // TaxPayment 엔티티 저장
        taxPaymentDTO.setStudentId(studentId);
        TaxPayment taxPayment = taxPaymentMapper.toEntity(taxPaymentDTO);
        TaxPayment savedTaxPayment = taxPaymentRepository.save(taxPayment);

        return taxPaymentMapper.toDto(savedTaxPayment);
    }

    // 특정 학생의 세금 납부 내역 조회
    public List<TaxPaymentDTO> findTaxPaymentsByStudent() {
        Long studentId = studentService.getCurrentStudentId();
        return taxPaymentRepository.findByStudentId(studentId).stream()
                .map(taxPaymentMapper::toDto)
                .collect(Collectors.toList());
    }

    // 특정 세금 유형에 대한 납부 내역 조회
    public List<TaxPaymentDTO> findTaxPaymentsByTax(Long taxId) {
        Long teacherId = teacherService.getCurrentTeacherId();
        return taxPaymentRepository.findByTeacherIdAndTaxId(teacherId, taxId).stream()
                .map(taxPaymentMapper::toDto)
                .collect(Collectors.toList());
    }

    // 특정 교사가 담당하는 학생들의 세금 납부 내역 조회
    public List<TaxPaymentDTO> findTaxPaymentsByTeacher() {
        Long teacherId = teacherService.getCurrentTeacherId();
        return taxPaymentRepository.findByTeacherId(teacherId).stream()
                .map(taxPaymentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 학생의 기간별 세급 납부 내역 조회
     */
    public List<TaxPaymentDTO> getTaxPaymentsByPeriod(Long studentId, LocalDate startDate, LocalDate endDate) {
        // 시작일이 종료일보다 이후일 경우 오류 발생
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 이후일 수 없습니다.");
        }

        // startDate가 없으면 endDate 이전 전체 내역 조회
        if (startDate == null && endDate != null) {
            startDate = LocalDate.of(1900, 1, 1); // 기본 시작일 설정
        }

        // endDate가 없으면 startDate 이후 전체 내역 조회
        if (endDate == null && startDate != null) {
            endDate = LocalDate.now(); // 기본 종료일 설정
        }

        // startDate와 endDate 모두 없으면 이번 달 내역 조회 (기본값)
        if (startDate == null && endDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
            endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        }

        // 최신순으로 정렬하여 세금 납부 내역 조회
        List<TaxPayment> payments = taxPaymentRepository.findByStudentIdAndPaymentDateBetweenOrderByPaymentDateDesc(studentId, startDate, endDate);

        // TaxPayment 엔티티를 TaxPaymentDTO로 변환 후 반환
        return payments.stream()
                .map(taxPaymentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 교사가 담당하는 학생들의 세금 납부 내역
     */
    public List<TeacherStudentTaxPaymentDTO> getStudentTaxPaymentListByTeacher(Long teacherId) {
        return taxPaymentRepository.findStudentTaxPaymentsByTeacherId(teacherId);
    }

    /**
     * 학생의 세금 납부 내역
     */
    public TeacherStudentTaxPaymentDTO getStudentTaxPaymentByStudent(Long studentId) {
        return taxPaymentRepository.findStudentTaxPaymentByStudentId(studentId);
    }

    /**
     * 특정 학생의 미납 세금 내역 조회
     */
    public OverdueTaxPaymentDTO getOverdueTaxPayments(Long studentId) {
        OverdueTaxPaymentDTO.OverdueTaxPaymentProjection projection = taxPaymentRepository.findOverdueTaxPaymentByStudentId(studentId);

        // Projection이 없을 경우 기본값 반환
        if (projection.getTaxNames() == null) {
            return new OverdueTaxPaymentDTO("벌금", "", 0);
        }

        return new OverdueTaxPaymentDTO(
                projection.getDescription(),
                projection.getTaxNames() + "에 대해 미납되었습니다.",
                projection.getTotalAmount()
        );
    }

    /**
     * 학생의 미납 세금 overdue 상태를 false(완납)로 업데이트
     */
    @Transactional
    public int clearOverdueTaxPayments(Long studentId) {
        OverdueTaxPaymentDTO overdueTaxPaymentDTO = getOverdueTaxPayments(studentId);

        if (overdueTaxPaymentDTO.getTotalAmount() == 0) {
            throw new IllegalArgumentException("미납된 세금이 없습니다.");
        }

        Long bankAccountId = studentService.findBankAccountIdById(studentId);
        BankAccountDTO bankAccountDTO = bankAccountService.findBankAccount(bankAccountId);

        int amount = overdueTaxPaymentDTO.getTotalAmount();

        if (bankAccountDTO.getBalance() < amount) {
            throw new IllegalArgumentException("현재 은행 계좌 잔액이 부족합니다. (현재 잔액: " + bankAccountDTO.getBalance() + ")");
        }

        // 미납된 적금 납부 처리
        BankTransactionDTO bankTransactionDTO = BankTransactionDTO.builder()
                .bankAccountId(bankAccountDTO.getId())
                .type(BankTransactionType.FINE)
                .amount(amount)
                .description("[벌금] 미납 세급 납부")
                .build();
        bankTransactionService.addBankTransaction(bankTransactionDTO);

        return taxPaymentRepository.updateOverdueToPaidByStudentId(studentId); // 업데이트된 행 수 반환
    }

}
