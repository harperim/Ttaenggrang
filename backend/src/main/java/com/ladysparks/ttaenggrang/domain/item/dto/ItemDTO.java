package com.ladysparks.ttaenggrang.domain.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ladysparks.ttaenggrang.domain.item.entity.SellerType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@JsonIgnoreProperties(value = {"id", "sellerId", "sellerName", "sellerType", "createdAt", "updatedAt", "approved"}, allowGetters = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemDTO {

    private Long id;

    private Long sellerId;

    private String sellerName;

    private SellerType sellerType;

    @NotNull(message = "상품명(name)은 필수 항목입니다.")
    private String name;

    private String description;

    private String image;

    @NotNull(message = "상품 가격(price)은 필수 항목입니다.")
    @Min(value = 1, message = "상품 가격(price)은 1 이상이어야 합니다.")
    private int price;

    @NotNull(message = "판매 수량(quantity)은 필수 항목입니다.")
    @Min(value = 1, message = "판매 수량(quantity)은 1 이상이어야 합니다.")
    private int quantity;

    private boolean approved;

    private Timestamp createdAt;
    private Timestamp updatedAt;

}
