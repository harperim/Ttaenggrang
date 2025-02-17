package com.ladysparks.ttaenggrang.domain.bank.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@JsonIgnoreProperties(value = {"id", "createdAt"}, allowGetters = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BankAccountDTO {

    private Long id;

    @NotNull(message = "계좌 번호(accountNumber)는 필수 항목입니다.")
    private String accountNumber;

    private int balance;
    
    private Timestamp createdAt;

}
