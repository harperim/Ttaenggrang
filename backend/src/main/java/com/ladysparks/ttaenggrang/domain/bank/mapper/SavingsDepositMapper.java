package com.ladysparks.ttaenggrang.domain.bank.mapper;

import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsDepositDTO;
import com.ladysparks.ttaenggrang.domain.bank.dto.SavingsDepositHistoryDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.SavingsDeposit;
import com.ladysparks.ttaenggrang.global.config.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface SavingsDepositMapper {

    @Mapping(source = "savingsSubscription.id", target = "savingsSubscriptionId")
    SavingsDepositDTO toDto(SavingsDeposit entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "savingsSubscriptionId", target = "savingsSubscription.id")
    SavingsDeposit toEntity(SavingsDepositDTO dto);

    @Mapping(target = "id", source = "id")
    @Mapping(source = "savingsSubscriptionId", target = "savingsSubscription.id")
    SavingsDeposit toUpdatedEntity(SavingsDepositDTO dto);

    @Mapping(source = "savingsSubscription.startDate", target = "startDate")
    @Mapping(source = "savingsSubscription.endDate", target = "maturityDate")
    @Mapping(source = "savingsSubscription.savingsProduct.interestRate", target = "interestRate")
    @Mapping(source = "createdAt", target = "transactionDate")
    @Mapping(target = "transactionType", expression = "java(savingsDeposit.getStatus().name())")
    @Mapping(target = "balance", expression = "java(calculateBalance(savingsDeposit))")
    SavingsDepositHistoryDTO toHistoryDto(SavingsDeposit savingsDeposit);

    default int calculateBalance(SavingsDeposit savingsDeposit) {
        int totalDeposits = savingsDeposit.getSavingsSubscription()
                .getSavingsProduct()
                .getAmount();

        float interestRate = savingsDeposit.getSavingsSubscription()
                .getSavingsProduct()
                .getInterestRate() / 100;

        return (int) (totalDeposits + (totalDeposits * interestRate));
    }

}