package com.ladysparks.ttaenggrang.domain.tax.mapper;

import com.ladysparks.ttaenggrang.domain.tax.dto.TeacherStudentTaxPaymentDTO;
import com.ladysparks.ttaenggrang.domain.tax.entity.TaxPayment;
import com.ladysparks.ttaenggrang.global.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface TeacherStudentTaxPaymentMapper {

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.name", target = "studentName")
    @Mapping(source = "amount", target = "totalAmount")
    TeacherStudentTaxPaymentDTO toDto(TaxPayment taxPayment);

}

