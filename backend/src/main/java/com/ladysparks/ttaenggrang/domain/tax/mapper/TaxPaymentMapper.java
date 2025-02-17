package com.ladysparks.ttaenggrang.domain.tax.mapper;

import com.ladysparks.ttaenggrang.domain.tax.dto.TaxPaymentDTO;
import com.ladysparks.ttaenggrang.domain.tax.entity.TaxPayment;
import com.ladysparks.ttaenggrang.global.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface TaxPaymentMapper {

    TaxPaymentMapper INSTANCE = Mappers.getMapper(TaxPaymentMapper.class);

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "tax.id", target = "taxId")
    @Mapping(source = "tax.taxName", target = "taxName")
    @Mapping(source = "tax.taxRate", target = "taxRate")
    @Mapping(source = "tax.taxDescription", target = "taxDescription")
    @Mapping(source = "overdue", target = "isOverdue")
    TaxPaymentDTO toDto(TaxPayment entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "studentId", target = "student.id")
    @Mapping(source = "taxId", target = "tax.id")
    TaxPayment toEntity(TaxPaymentDTO taxPaymentDTO);

}
