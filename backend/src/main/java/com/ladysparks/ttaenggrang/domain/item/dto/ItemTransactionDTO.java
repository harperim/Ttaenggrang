package com.ladysparks.ttaenggrang.domain.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@JsonIgnoreProperties(value = {"id", "buyerId", "itemName", "itemPrice", "itemDescription", "createdAt"}, allowGetters = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemTransactionDTO {

    private Long id;

    @NotNull(message = "상품 ID(itemId)는 필수 항목입니다.")
    private Long itemId;

    private Long buyerId;

    private String itemName;

    private int itemPrice;

    private String itemDescription;

    @NotNull(message = "구매 수량(quantity)은 필수 항목입니다.")
    @Min(value = 1, message = "구매 수량(quantity)은 1 이상이어야 합니다.")
    private int quantity;

    private Timestamp createdAt;

}
