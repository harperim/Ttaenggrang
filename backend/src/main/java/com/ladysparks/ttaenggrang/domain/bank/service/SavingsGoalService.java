package com.ladysparks.ttaenggrang.domain.bank.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsGoalDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsGoal;
import com.ladysparks.ttaenggrang.domain.bank.mapper.SavingsGoalMapper;
import com.ladysparks.ttaenggrang.domain.bank.repository.SavingsGoalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final SavingsGoalMapper savingsGoalMapper;

    @Autowired
    public SavingsGoalService(SavingsGoalRepository savingsGoalRepository, SavingsGoalMapper savingsGoalMapper) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.savingsGoalMapper = savingsGoalMapper;
    }

    @Transactional
    public SavingsGoalDTO addSavingsGoal(SavingsGoalDTO savingsGoalDTO) {
        if (savingsGoalDTO == null) {
            throw new IllegalArgumentException("요청된 저축 목표 정보가 유효하지 않습니다.");
        }

        // 1. 필수 값 검증
        if (savingsGoalDTO.getStudentId() == null || savingsGoalDTO.getName() == null) {
            throw new IllegalArgumentException("studentId, name은 필수 입력 값입니다.");
        }

        // 2. 중복 목표 방지 (같은 학생이 동일한 목표를 중복 등록할 수 없음)
        boolean exists = savingsGoalRepository.existsByStudentIdAndName(
                savingsGoalDTO.getStudentId(), savingsGoalDTO.getName());

        if (exists) {
            throw new IllegalArgumentException("해당 저축 목표가 이미 존재합니다. (학생 ID: " + savingsGoalDTO.getStudentId() + ", 목표명: " + savingsGoalDTO.getName() + ")");
        }

        // 3. Entity 변환 및 저장
        SavingsGoal savingsGoal = savingsGoalMapper.toEntity(savingsGoalDTO);
        SavingsGoal savedSavingsGoal = savingsGoalRepository.save(savingsGoal);

        return savingsGoalMapper.toDto(savedSavingsGoal);
    }

    public List<SavingsGoalDTO> findSavingsGoals(Long studentId) {
        List<SavingsGoal> savingsGoals = savingsGoalRepository.findByStudentId(studentId);

        if (savingsGoals.isEmpty()) {
            throw new EntityNotFoundException("해당 학생의 저축 목표가 존재하지 않습니다. ID: " + studentId);
        }

        return savingsGoals.stream()
                .map(savingsGoalMapper::toDto)
                .collect(Collectors.toList());
    }

}
