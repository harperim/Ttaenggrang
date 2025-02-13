package com.ladysparks.ttaenggrang.domain.item.service;

import com.ladysparks.ttaenggrang.domain.bank.dto.BankTransactionDTO;
import com.ladysparks.ttaenggrang.domain.bank.entity.BankTransaction.BankTransactionType;
import com.ladysparks.ttaenggrang.domain.bank.service.BankTransactionService;
import com.ladysparks.ttaenggrang.domain.item.entity.Item;
import com.ladysparks.ttaenggrang.domain.item.entity.ItemTransaction;
import com.ladysparks.ttaenggrang.domain.item.dto.ItemTransactionDTO;
import com.ladysparks.ttaenggrang.domain.item.entity.SellerType;
import com.ladysparks.ttaenggrang.domain.item.mapper.ItemTransactionMapper;
import com.ladysparks.ttaenggrang.domain.item.repository.ItemRepository;
import com.ladysparks.ttaenggrang.domain.item.repository.ItemTransactionRepository;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemTransactionService {

    private final ItemRepository itemRepository;
    private final ItemTransactionRepository itemTransactionRepository;
    private final ItemTransactionMapper itemTransactionMapper;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final BankTransactionService bankTransactionService;

    // 아이템 거래
    @Transactional
    public ItemTransactionDTO addItemTransaction(ItemTransactionDTO itemTransactionDTO) {
        // 1. 구매할 아이템 조회
        Item item = itemRepository.findById(itemTransactionDTO.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다. ID: " + itemTransactionDTO.getItemId()));

        // 2. 판매자와 구매자 조회
        Long sellerId = item.getSellerType() == SellerType.STUDENT ? item.getSellerStudent().getId() : item.getSellerTeacher().getId();

        Long buyerId = studentService.getCurrentStudentId();

        // 3. 판매자와 구매자가 동일한 경우 거래 불가
        if (sellerId.equals(buyerId)) {
            throw new IllegalArgumentException("판매자와 구매자는 동일할 수 없습니다. (사용자 ID: " + buyerId + ")");
        }

        // 4. 남은 수량 계산 및 검증
        int remainingQuantity = item.getQuantity() - itemTransactionDTO.getQuantity();
        if (remainingQuantity < 0) {
            throw new IllegalArgumentException("해당 상품의 재고가 부족합니다. 재고: " + item.getQuantity());
        }

        // 5. 재고 차감 후 업데이트
        item.updateQuantity(remainingQuantity);
        itemRepository.save(item);

        // 6. 기존 거래 내역 확인
        Optional<ItemTransaction> existingTransaction = itemTransactionRepository.findByItemIdAndBuyerId(item.getId(), buyerId);

        ItemTransaction savedItemTransaction;
        if (existingTransaction.isPresent()) {
            // 기존 거래가 있으면 수량 업데이트
            ItemTransaction itemTransaction = existingTransaction.get();
            itemTransaction.updateQuantity(itemTransaction.getQuantity() + itemTransactionDTO.getQuantity());
            savedItemTransaction = itemTransactionRepository.save(itemTransaction);
        } else {
            // 기존 거래가 없으면 새로운 거래 생성
            itemTransactionDTO.setBuyerId(buyerId);
            ItemTransaction itemTransaction = itemTransactionMapper.toEntity(itemTransactionDTO);
            savedItemTransaction = itemTransactionRepository.save(itemTransaction);
        }

        // 7. 은행 계좌 거래
        BankTransactionDTO bankTransactionDTO = BankTransactionDTO.builder()
                .bankAccountId(buyerId)
                .type(BankTransactionType.ITEM)
                .amount(itemTransactionDTO.getQuantity() * item.getPrice())
                .description("[상품 거래] 상품명: " + item.getName())
                .receiverId(sellerId)
                .build();
        bankTransactionService.addBankTransaction(bankTransactionDTO);

        return itemTransactionMapper.toDto(savedItemTransaction);
    }

    // 모든 판매 내역 조회
    public List<ItemTransactionDTO> findItemTransactionsBySeller() {
        Optional<Long> currentTeacherId = teacherService.getOptionalCurrentTeacherId();
        Optional<Long> currentStudentId = studentService.getOptionalCurrentStudentId();

        if (currentTeacherId.isPresent()) { // 교사 로그인
            return itemTransactionRepository.findByItem_SellerTeacher_id(currentTeacherId.get())
                    .stream()
                    .map(itemTransactionMapper::toDto)
                    .collect(Collectors.toList());
        } else if (currentStudentId.isPresent()) { // 학생 로그인
            return itemTransactionRepository.findByItem_SellerStudent_id(currentStudentId.get())
                    .stream()
                    .map(itemTransactionMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
        }
    }

    // 학생의 모든 구매 내역 조회
    public List<ItemTransactionDTO> findItemTransactionsByBuyer() {
        Long buyerId = studentService.getCurrentStudentId();
        return itemTransactionRepository.findByBuyerId(buyerId)
                .stream()
                .filter(transaction -> transaction.getQuantity() > 0) // quantity가 0인 경우 제외
                .map(itemTransactionMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 아이템 사용 (수량 차감 및 트랜잭션 기록)
     */
    public ItemTransactionDTO useItem(Long itemTransactionId) {
        // 아이템 거래 내역 조회
        ItemTransaction itemTransaction = itemTransactionRepository.findById(itemTransactionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품 거래 내역이 존재하지 않습니다."));

        // 수량 차감
        if (itemTransaction.getQuantity() - 1 < 0) {
            throw new IllegalArgumentException("남은 수량이 부족합니다.");
        }

        itemTransaction.updateQuantity(itemTransaction.getQuantity() - 1);
        itemTransactionRepository.save(itemTransaction);

        return itemTransactionMapper.toDto(itemTransaction);
    }

}
