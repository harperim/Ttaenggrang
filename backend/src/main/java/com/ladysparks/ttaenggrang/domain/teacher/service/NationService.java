package com.ladysparks.ttaenggrang.domain.teacher.service;

import com.ladysparks.ttaenggrang.domain.teacher.dto.NationDTO;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Nation;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import com.ladysparks.ttaenggrang.domain.teacher.mapper.NationMapper;
import com.ladysparks.ttaenggrang.domain.teacher.repository.NationRepository;
import com.ladysparks.ttaenggrang.domain.teacher.repository.TeacherRepository;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NationService {

    private final NationMapper nationMapper;
    private final NationRepository nationRepository;
    private final TeacherRepository teacherRepository;

    public ApiResponse<NationDTO> createNation(Long teacherId, NationDTO nationDTO) {
        // 1. 교사 엔티티 조회
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 교사를 찾을 수 없습니다."));

        // 2. 교사가 이미 국가를 가지고 있는지 확인
        boolean exist = nationRepository.existsByTeacherId(teacherId);
        if (exist) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "이미 국가가 등록되어 있습니다.", null);
        }

        // 3. 국가 정보 저장
        Nation nation = Nation.builder()
                .nationName(nationDTO.getNationName())
                .population(nationDTO.getPopulation())
                .currency(nationDTO.getCurrency())
                .savingsGoalAmount(
                        nationDTO.getSavingsGoalAmount() != null ? nationDTO.getSavingsGoalAmount() : 100000)
                .establishedDate(
                        nationDTO.getEstablishedDate() != null
                                ? nationDTO.getEstablishedDate()
                                : new Timestamp(System.currentTimeMillis())  // 현재 시각으로 기본값 설정
                )
                .nationalTreasury(0)
                .teacher(teacher)
                .build();
        Nation savedNation = nationRepository.save(nation);

        // 4. 응답 데이터 생성
        NationDTO responseDTO = nationMapper.toDto(savedNation);

        return ApiResponse.created("국가 정보 등록이 완료되었습니다.", responseDTO);
    }

    // 국가 [조회]
    public ApiResponse<NationDTO> getNationByTeacherId(Long teacherId) {
        // 1. 교사 엔티티 조회
//        Long teacherId = teacherService.getCurrentTeacherId();

        // 2. 교사가 이미 국가를 가지고 있는지 확인
        NationDTO nationDTO = findNationByTeacherId(teacherId)
                .orElseThrow(() -> new NotFoundException("등록된 국가가 없습니다."));

        Nation nation = nationMapper.toEntity(nationDTO);
        NationDTO responseDTO = nationMapper.toDto(nation);

        return ApiResponse.success("국가 정보 조회 성공", responseDTO);
    }

    @PersistenceContext
    private EntityManager entityManager;

    // 국가 [삭제]
    @Transactional
    public ApiResponse<Void> deleteNation(Long teacherId) {
        // 1. 교사 엔티티 조회
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 교사를 찾을 수 없습닌다."));

        // 2. 교사가 이미 국가를 가지고 있는지 확인
        Optional<NationDTO> nationDTO = findNationByTeacherId(teacherId);

        if (nationDTO.isPresent()) {
            // 3. 국가 삭제
            nationRepository.deleteById(nationDTO.get().getId());

            // 4. 강제 DB 반영
            entityManager.flush();

            // 5. 성공 응답 반환
            return ApiResponse.success("국가 정보가 삭제되었습니다.", null);
        } else {
            throw new NotFoundException("등록된 국가가 없습니다.");
        }
    }

    @Transactional
    public NationDTO updateNationFunding(Long nationId, int funding) {
        // Nation 조회
        Nation nation = nationRepository.findById(nationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 국가를 찾을 수 없습니다."));

        // funding 값 업데이트
        nation.setNationalTreasury(nation.getNationalTreasury() + funding);

        Nation updatedNation = nationRepository.save(nation);

        // NationCreateDTO로 변환 후 반환
        return nationMapper.toDto(updatedNation);
    }

    public Optional<NationDTO> findNationByTeacherId(Long teacherId) {
        return nationRepository.findByTeacher_Id(teacherId)
                .map(nationMapper::toDto);
    }

    public int findSavingsGoalAmountByTeacherId(Long teacherId) {
        return nationRepository.findSavingsGoalAmountByTeacher_Id(teacherId).orElse(0);
    }

}
