package com.ladysparks.ttaenggrang.domain.bank.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositAndSavingsCountDTO {

    private long depositProductCount; // 예금 상품 개수
    private long savingsProductCount; // 적금 상품 개수

}
