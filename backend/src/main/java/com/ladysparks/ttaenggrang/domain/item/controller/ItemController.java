package com.ladysparks.ttaenggrang.domain.item.controller;

import com.ladysparks.ttaenggrang.domain.item.dto.ItemDTO;
import com.ladysparks.ttaenggrang.domain.item.service.ItemService;
import com.ladysparks.ttaenggrang.global.docs.item.ItemApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item-products")
public class ItemController implements ItemApiSpecification {

    private final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;

    // 판매 아이템 [등록]
    @PostMapping
    public ResponseEntity<ApiResponse<ItemDTO>> itemAdd(@RequestBody @Valid ItemDTO itemDto) {
        ItemDTO savedItem = itemService.addItem(itemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedItem));
    }

    // 판매 아이템 [전체 조회]
    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemDTO>>> itemList() {
        List<ItemDTO> itemDtoList = itemService.findItemList();
        return ResponseEntity.ok(ApiResponse.success(itemDtoList));
    }

    // 판매 아이템 [상세 조회]
    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ItemDTO>> itemDetails(@PathVariable("itemId") Long itemId) {
        ItemDTO itemDTO = itemService.findItem(itemId);
        return ResponseEntity.ok(ApiResponse.success(itemDTO));
    }

    // 학생의 판매 아이템 [조회]
    @GetMapping("/sale")
    public ResponseEntity<ApiResponse<List<ItemDTO>>> itemListBySeller() {
        List<ItemDTO> itemDtoList = itemService.findItemListBySeller();
        return ResponseEntity.ok(ApiResponse.success(itemDtoList));
    }

    // 교사가 담당하는 학급의 판매 아이템 내역 [전체 조회]
//    @GetMapping("/teacher")
//    public ResponseEntity<ApiResponse<List<ItemDTO>>> itemListByTeacher() {
//        List<ItemDTO> itemDTOList = itemService.findItemListByTeacher();
//        return ResponseEntity.ok(ApiResponse.success(itemDTOList));
//    }

}

