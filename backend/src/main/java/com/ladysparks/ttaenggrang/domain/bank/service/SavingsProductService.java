package com.ladysparks.ttaenggrang.domain.bank.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.DepositAndSavingsCountDTO;
import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsProductDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsProduct;
import com.ladysparks.ttaenggrang.domain.bank.mapper.SavingsProductMapper;
import com.ladysparks.ttaenggrang.domain.bank.repository.SavingsProductRepository;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingsProductService {

    private final SavingsProductRepository savingsProductRepository;
    private final SavingsProductMapper savingsProductMapper;
    private final TeacherService teacherService;
    private final StudentService studentService;

    // 적금 상품 [등록]
    @Transactional
    public SavingsProductDTO addSavingsProducts(SavingsProductDTO savingsProductDTO) {
        if (savingsProductDTO == null) {
            throw new IllegalArgumentException("요청된 적금 상품 정보가 유효하지 않습니다.");
        }

        Long teacherId = teacherService.getCurrentTeacherId();

        // 중복 적금 상품 방지 (같은 교사가 같은 이름의 적금 상품을 등록할 수 없음)
        boolean exists = savingsProductRepository.existsByTeacherIdAndName(
                teacherId, savingsProductDTO.getName());

        if (exists) {
            throw new IllegalArgumentException("해당 적금 상품이 이미 존재합니다. (교사 ID: " + teacherId + ", 상품명: " + savingsProductDTO.getName() + ")");
        }

        savingsProductDTO.setTeacherId(teacherId);
        SavingsProduct savingsProduct = savingsProductMapper.toEntity(savingsProductDTO);
        SavingsProduct savedSavingsProduct = savingsProductRepository.save(savingsProduct);

        return savingsProductMapper.toDto(savedSavingsProduct);
    }

    // 적금 상품 [조회]
    public List<SavingsProductDTO> findSavingsProducts() {
        Optional<Long> currentTeacherId = teacherService.getOptionalCurrentTeacherId();
        Optional<Long> currentStudentId = studentService.getOptionalCurrentStudentId();

        Long teacherId = 0L;
        if (currentTeacherId.isPresent()) { // 교사 로그인
            teacherId = currentTeacherId.get();
        } else if (currentStudentId.isPresent()) { // 학생 로그인
            teacherId = studentService.findTeacherIdByStudentId(currentStudentId.get());
        } else {
            throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
        }

        List<SavingsProduct> savingsProducts = savingsProductRepository.findByTeacherId(teacherId);

        if (savingsProducts.isEmpty()) {
            throw new EntityNotFoundException("해당 교사의 적금 상품이 존재하지 않습니다. ID: " + teacherId);
        }

        return savingsProducts.stream()
                .map(savingsProductMapper::toDto)
                .collect(Collectors.toList());
    }

    public Long findDurationWeeksById(Long savingsProductId) {
        return savingsProductRepository.findDurationWeeksById(savingsProductId)
                .orElseThrow(() -> new IllegalIdentifierException("해당 적금 상품이 존재하지 않습니다. ID: " + savingsProductId));
    }

    /**
     * 구독자 수 증가
     */
    public int addSubscriber(Long savingsProductId) {
        return savingsProductRepository.incrementSubscriberCount(savingsProductId);
    }

    /**
     * 구독자 수 감소
     */
    public int removeSubscriber(Long savingsProductId) {
        return savingsProductRepository.decrementSubscriberCount(savingsProductId);
    }

    /**
     * 💳 특정 교사가 등록한 예금 및 적금 상품 개수 조회
     */
    public DepositAndSavingsCountDTO getDepositAndSavingsCountsByTeacherId(Long teacherId) {
        long depositCount = 0;
        long savingsCount = savingsProductRepository.countSavingsProductsByTeacherId(teacherId);

        return DepositAndSavingsCountDTO.builder()
                .depositProductCount(depositCount)
                .savingsProductCount(savingsCount)
                .build();
    }

}
