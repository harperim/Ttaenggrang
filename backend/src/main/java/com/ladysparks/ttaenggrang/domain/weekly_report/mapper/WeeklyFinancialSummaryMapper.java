package com.ladysparks.ttaenggrang.domain.weekly_report.mapper;

import com.ladysparks.ttaenggrang.domain.weekly_report.dto.WeeklyFinancialSummaryDTO;
import com.ladysparks.ttaenggrang.domain.weekly_report.entity.WeeklyFinancialSummary;
import com.ladysparks.ttaenggrang.global.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface WeeklyFinancialSummaryMapper {

    WeeklyFinancialSummaryMapper INSTANCE = Mappers.getMapper(WeeklyFinancialSummaryMapper.class);

    /**
     * WeeklyFinancialSummary 엔티티를 DTO로 변환
     */
    @Mapping(source = "student.id", target = "studentId")
    WeeklyFinancialSummaryDTO toDto(WeeklyFinancialSummary summary);

    /**
     * WeeklyFinancialSummaryDTO를 엔티티로 변환
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "studentId", target = "student.id")
    WeeklyFinancialSummary toEntity(WeeklyFinancialSummaryDTO dto);

    // 기존 엔티티 업데이트
    void updateFromDto(WeeklyFinancialSummaryDTO dto, @MappingTarget WeeklyFinancialSummary entity);

}

