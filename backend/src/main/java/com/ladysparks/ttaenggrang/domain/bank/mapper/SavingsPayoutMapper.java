package com.ladysparks.ttaenggrang.domain.bank.mapper;

import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsPayoutDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsPayout;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SavingsPayoutMapper {

    @Mapping(source = "savingsSubscription.id", target = "savingsSubscriptionId")
    @Mapping(source = "savingsSubscription.depositAmount", target = "principalAmount") // 원금 매핑
    @Mapping(source = "paid", target = "paid") // boolean 필드 매핑
    SavingsPayoutDTO toDto(SavingsPayout savingsPayout);

    @Mapping(target = "id", ignore = true) // ID는 자동 생성되므로 무시
    @Mapping(source = "savingsSubscriptionId", target = "savingsSubscription.id")
    @Mapping(source = "paid", target = "paid") // DTO -> Entity 변환 시 boolean 필드 매핑
    SavingsPayout toEntity(SavingsPayoutDTO savingsPayoutDTO);
}
