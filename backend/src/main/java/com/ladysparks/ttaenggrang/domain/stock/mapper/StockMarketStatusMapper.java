package com.ladysparks.ttaenggrang.domain.stock.mapper;

import com.ladysparks.ttaenggrang.domain.stock.dto.StockMarketStatusDTO;
import com.ladysparks.ttaenggrang.domain.stock.entity.StockMarketStatus;
import com.ladysparks.ttaenggrang.global.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface StockMarketStatusMapper {

    StockMarketStatusMapper INSTANCE = Mappers.getMapper(StockMarketStatusMapper.class);

    // Entity -> DTO 변환
    @Mapping(source = "teacher.id", target = "teacherId")
    StockMarketStatusDTO toDto(StockMarketStatus marketStatus);

    // DTO -> Entity 변환
    @Mapping(source = "teacherId", target = "teacher.id")
    StockMarketStatus toEntity(StockMarketStatusDTO marketStatusDTO);

}
