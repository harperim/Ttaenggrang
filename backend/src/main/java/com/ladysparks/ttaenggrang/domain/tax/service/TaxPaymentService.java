package com.ladysparks.ttaenggrang.domain.tax.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.BankAccountDTO;
import com.ladysparks.ttaenggrang.domain.bank.dto.BankTransactionDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.service.BankAccountService;
import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
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

}
