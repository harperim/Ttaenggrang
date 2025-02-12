package com.ladysparks.ttaenggrang.domain.bank.mapper;

import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsPayoutDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsPayout;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SavingsPayoutMapper {

    @Mapping(source = "savingsSubscription.id", target = "savingsSubscriptionId")
    SavingsPayoutDTO toDto(SavingsPayout savingsPayout);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "savingsSubscriptionId", target = "savingsSubscription.id")
    SavingsPayout toEntity(SavingsPayoutDTO savingsPayoutDTO);

}