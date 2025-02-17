package com.ladysparks.ttaenggrang.domain.tax.mapper;

import com.ladysparks.ttaenggrang.domain.tax.dto.TaxDTO;
import com.ladysparks.ttaenggrang.domain.tax.entity.Tax;
import com.ladysparks.ttaenggrang.global.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface TaxMapper {

    TaxMapper INSTANCE = Mappers.getMapper(TaxMapper.class);

    @Mapping(source = "teacher.id", target = "teacherId")
    TaxDTO toDto(Tax tax);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "teacherId", target = "teacher.id")
    Tax toEntity(TaxDTO taxDTO);

}
