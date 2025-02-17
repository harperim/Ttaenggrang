package com.ladysparks.ttaenggrang.domain.bank.mapper;

import com.ladysparks.ttaenggrang.global.config.MapStructConfig;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsProduct;
import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface SavingsProductMapper {

    SavingsProductMapper INSTANCE = Mappers.getMapper(SavingsProductMapper.class);

    @Mapping(source = "teacher.id", target = "teacherId")
    SavingsProductDTO toDto(SavingsProduct savingsProduct);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "teacherId", target = "teacher.id")
    SavingsProduct toEntity(SavingsProductDTO savingsProductDTO);

}
