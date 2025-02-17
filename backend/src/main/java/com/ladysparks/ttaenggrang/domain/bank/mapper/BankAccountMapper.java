package com.ladysparks.ttaenggrang.domain.bank.mapper;

import com.ladysparks.ttaenggrang.global.config.MapStructConfig;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankAccount;
import com.ladysparks.ttaenggrang.domain.bank.dto.BankAccountDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface BankAccountMapper {

    BankAccountMapper INSTANCE = Mappers.getMapper(BankAccountMapper.class);

    // Entity → DTO 변환
    BankAccountDTO toDto(BankAccount bankAccount);

    // DTO → Entity 변환 (새로운 계좌 등록 시 사용)
    @Mapping(target = "id", ignore = true) // ID는 자동 생성되므로 무시
    BankAccount toEntity(BankAccountDTO bankAccountDTO);

    // DTO → Entity 변환 (기존 계좌 정보 업데이트 (id 유지))
    @Mapping(target = "id", source = "id") // ID를 유지하도록 매핑
    BankAccount toUpdatedEntity(BankAccountDTO bankAccountDTO);

}
