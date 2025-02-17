package com.ladysparks.ttaenggrang.domain.bank.mapper;

import com.ladysparks.ttaenggrang.global.config.MapStructConfig;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsGoal;
import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsGoalDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface SavingsGoalMapper {

    SavingsGoalMapper INSTANCE = Mappers.getMapper(SavingsGoalMapper.class);

    @Mapping(source = "student.id", target = "studentId")
    SavingsGoalDTO toDto(SavingsGoal savingsGoal);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "studentId", target = "student.id")
    SavingsGoal toEntity(SavingsGoalDTO savingsGoalDTO);

}
