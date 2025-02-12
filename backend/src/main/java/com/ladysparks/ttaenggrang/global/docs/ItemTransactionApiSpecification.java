package com.ladysparks.ttaenggrang.global.docs;

import com.ladysparks.ttaenggrang.domain.item.dto.ItemTransactionDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Item-Transaction", description = "상품 거래 관련 API")
public interface ItemTransactionApiSpecification {

    @Operation(summary = "상품 구매 [등록]", description = """
            💡학생이 상품을 구매합니다.

            **[ 요청 필드 ]**
            - **itemId** : 상품 ID
            - **quantity** : 구매 수량
            
            **[ 규칙 ]**
            - 상품 ID, 구매 수량은 필수 항목입니다.
            - 구매 수량은 1개 이상이어야 합니다.
            """)
    ResponseEntity<ApiResponse<ItemTransactionDTO>> itemTransactionAdd(@RequestBody ItemTransactionDTO itemTransactionDTO);

    @Operation(summary = "상품 판매 내역 [전체 조회]", description = """
            💡학생의 상품 판매 내역을 조회합니다.
            
            **[ 응답 필드 ]**
            - **id** : 상품 거래 ID
            - **buyerId** : 구매자 학생 ID
            - **itemName** : 상품명
            - **itemPrice** : 상품 가격
            - **quantity** : 구매 수량
            - **createdAt** : 상품 구매일
            """)
    ResponseEntity<ApiResponse<List<ItemTransactionDTO>>> itemTransactionBySellerList();

    @Operation(summary = "상품 구매 내역 [전체 조회]", description = """
            💡학생의 상품 구매 내역을 조회합니다.

            **[ 응답 필드 ]**
            - **id** : 상품 거래 ID
            - **buyerId** : 구매자 학생 ID
            - **itemName** : 상품명
            - **itemPrice** : 상품 가격
            - **quantity** : 구매 수량
            - **createdAt** : 상품 구매일
            
            **[ 규칙 ]**
            - 구매 수량이 0개가 되면 목록에서 보여지지 않습니다.
            """)
    ResponseEntity<ApiResponse<List<ItemTransactionDTO>>> itemTransactionByBuyerList();

    @Operation(summary = "상품 사용 [수정]", description = """
            💡학생이 상품을 사용합니다.

            **[ 응답 필드 ]**
            - **id** : 상품 거래 ID
            - **buyerId** : 구매자 학생 ID
            - **itemName** : 상품명
            - **itemPrice** : 상품 가격
            - **quantity** : 구매 수량
            - **createdAt** : 상품 구매일
            
            **[ 규칙 ]**
            - 상품 사용 시 구매 수량이 1개씩 차감됩니다.
            - 구매 수량이 0개가 되면 목록에서 보여지지 않습니다.
            """)
    ResponseEntity<ApiResponse<ItemTransactionDTO>> useItem(@PathVariable Long itemTransactionId);

}
