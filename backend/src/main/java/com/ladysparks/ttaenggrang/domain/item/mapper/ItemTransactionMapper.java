package com.ladysparks.ttaenggrang.domain.item.mapper;

import com.ladysparks.ttaenggrang.global.config.MapStructConfig;
import com.ladysparks.ttaenggrang.domain.item.entity.ItemTransaction;
import com.ladysparks.ttaenggrang.domain.item.dto.ItemTransactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface ItemTransactionMapper {

    ItemTransactionMapper INSTANCE = Mappers.getMapper(ItemTransactionMapper.class);

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "buyer.id", target = "buyerId")
    @Mapping(source = "item.name", target = "itemName")
    @Mapping(source = "item.price", target = "itemPrice")
    @Mapping(source = "item.description", target = "itemDescription")
    ItemTransactionDTO toDto(ItemTransaction itemTransaction);

    @Mapping(target = "id", ignore = true) // ID는 자동 생성되므로 무시
    @Mapping(source = "itemId", target = "item.id")
    @Mapping(source = "buyerId", target = "buyer.id")
    ItemTransaction toEntity(ItemTransactionDTO itemTransactionDTO);

}
