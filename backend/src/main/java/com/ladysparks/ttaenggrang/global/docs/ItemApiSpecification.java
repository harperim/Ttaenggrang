package com.ladysparks.ttaenggrang.global.docs;

import com.ladysparks.ttaenggrang.domain.item.dto.ItemDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Item-Product", description = "아이템 상품 관련 API")
public interface ItemApiSpecification {

    @Operation(summary = "학급 내 판매 아이템 [등록]", description = """
            💡 (학생/교사) 판매할 아이템을 등록합니다.

            **[ 요청 필드 ]**
            - **name** : 상품명
            - **description** : 상품 설명
            - **image** : 상품 이미지 URL (S3 URL)
            - **price** : 상품 가격
            - **quantity** : 판매 수량
            
            **[ 규칙 ]**
            - 상품명, 상품 가격, 판매 수량은 필수 항목입니다.
            - 상품 이미지 URL은 AWS S3에 이미지를 업로드하고 생성된 URL입니다.
            - 로그인된 아이템
            """)
    ResponseEntity<ApiResponse<ItemDTO>> itemAdd(@RequestBody ItemDTO itemDto);

    @Operation(summary = "학급 내 판매 아이템 [전체 조회]", description = """
            💡 (학생/교사) 학급 내 전체 판매 아이템을 조회합니다.
            
            **[ 응답 필드 ]**
            - **id** : 아이템 ID
            - **sellerId** : 판매자 ID
            - **sellerName** : 판매자 이름
            - **sellerType**: 판매자 유형("STUDENT", "TEACHER")
            - **name** : 상품명
            - **description** : 상품 설명
            - **image** : 상품 이미지 URL (S3 URL)
            - **price** : 상품 가격
            - **quantity** : 판매 수량
            - **approved** : 교사 승인 여부
            - **createdAt** : 아이템 생성일
            - **updatedAt** : 아이템 수정일
            """)
    ResponseEntity<ApiResponse<List<ItemDTO>>> itemList();

    @Operation(summary = "학급 내 판매 아이템 [상세 조회]", description = """
            💡 판매 중인 아이템을 조회합니다.
            
            **[ 응답 필드 ]**
            - **id** : 아이템 ID
            - **sellerId** : 판매자 ID
            - **sellerName** : 판매자 이름
            - **sellerType**: 판매자 유형("STUDENT", "TEACHER")
            - **name** : 상품명
            - **description** : 상품 설명
            - **image** : 상품 이미지 URL (S3 URL)
            - **price** : 상품 가격
            - **quantity** : 판매 수량
            - **approved** : 교사 승인 여부
            - **createdAt** : 아이템 생성일
            - **updatedAt** : 아이템 수정일
            """)
    ResponseEntity<ApiResponse<ItemDTO>> itemDetails(@PathVariable("itemId") Long itemId);

    @Operation(summary = "판매 아이템 [전체 조회]", description = """
            💡 (학생/교사) 본인이 판매 중인 전체 아이템을 조회합니다.
            
            **[ 응답 필드 ]**
            - **id** : 아이템 ID
            - **sellerId** : 판매자 ID
            - **sellerName** : 판매자 이름
            - **sellerType**: 판매자 유형("STUDENT", "TEACHER")
            - **name** : 상품명
            - **description** : 상품 설명
            - **image** : 상품 이미지 URL (S3 URL)
            - **price** : 상품 가격
            - **quantity** : 판매 수량
            - **approved** : 교사 승인 여부
            - **createdAt** : 아이템 생성일
            - **updatedAt** : 아이템 수정일
            """)
    ResponseEntity<ApiResponse<List<ItemDTO>>> itemListBySeller();

//    @Operation(summary = "학급 내 판매 아이템 (교사) [전체 조회]", description = """
//            💡 교사가 학급 내 전체 판매 아이템을 조회합니다.
//
//            - **id** : 아이템 ID
//            - **sellerId** : 판매자 학생 ID
//            - **sellerName** : 판매자 학생 이름
//            - **name** : 상품명
//            - **description** : 상품 설명
//            - **image** : 상품 이미지 URL (S3 URL)
//            - **price** : 상품 가격
//            - **quantity** : 판매 수량
//            - **approved** : 교사 승인 여부
//            - **createdAt** : 아이템 생성일
//            - **updatedAt** : 아이템 수정일
//            """)
//    ResponseEntity<ApiResponse<List<ItemDTO>>> itemListByTeacher();

}