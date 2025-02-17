package com.ladysparks.ttaenggrang.domain.item.mapper;

import com.ladysparks.ttaenggrang.global.config.MapStructConfig;
import com.ladysparks.ttaenggrang.domain.item.entity.Item;
import com.ladysparks.ttaenggrang.domain.item.dto.ItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    // Entity → DTO 변환
    @Mapping(source = "sellerStudent.id", target = "sellerId")
    @Mapping(source = "sellerStudent.name", target = "sellerName")
    ItemDTO toStudentSellerDto(Item item);

    // DTO → Entity 변환
    @Mapping(target = "id", ignore = true) // ID는 자동 생성되므로 무시
    @Mapping(source = "sellerId", target = "sellerStudent.id")
    @Mapping(source = "sellerName", target = "sellerStudent.name")
    Item toStudentSellerEntity(ItemDTO itemDTO);

    // Entity → DTO 변환
    @Mapping(source = "sellerTeacher.id", target = "sellerId")
    @Mapping(source = "sellerTeacher.name", target = "sellerName")
    ItemDTO toTeacherSellerDto(Item item);

    // DTO → Entity 변환
    @Mapping(target = "id", ignore = true) // ID는 자동 생성되므로 무시
    @Mapping(source = "sellerId", target = "sellerTeacher.id")
    @Mapping(source = "sellerName", target = "sellerTeacher.name")
    Item toTeacherSellerEntity(ItemDTO itemDTO);

    // **공통 toDto 메서드 추가**
    default ItemDTO toDto(Item item) {
        if (item.getSellerTeacher() != null) {
            return toTeacherSellerDto(item);
        } else if (item.getSellerStudent() != null) {
            return toStudentSellerDto(item);
        } else {
            return null; // sellerTeacher와 sellerStudent가 없는 경우 처리
        }
    }

}
