package com.ladysparks.ttaenggrang.domain.item.service;

import com.ladysparks.ttaenggrang.domain.item.entity.Item;
import com.ladysparks.ttaenggrang.domain.item.dto.ItemDTO;
import com.ladysparks.ttaenggrang.domain.item.entity.SellerType;
import com.ladysparks.ttaenggrang.domain.item.mapper.ItemMapper;
import com.ladysparks.ttaenggrang.domain.item.repository.ItemRepository;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final StudentService studentService;
    private final TeacherService teacherService;

    // 상품 등록
    public ItemDTO addItem(ItemDTO itemDto) {
        Optional<Long> currentTeacherId = teacherService.getOptionalCurrentTeacherId();
        Optional<Long> currentStudentId = studentService.getOptionalCurrentStudentId();

        if (currentTeacherId.isPresent()) { // 교사 로그인
            itemDto.setSellerType(SellerType.TEACHER);
            itemDto.setSellerId(currentTeacherId.get());
            itemDto.setSellerName(teacherService.findNameById(currentTeacherId.get()));
            Item item = itemMapper.toTeacherSellerEntity(itemDto);
            Item savedItem = itemRepository.save(item);
            return itemMapper.toTeacherSellerDto(savedItem);
        } else if (currentStudentId.isPresent()) { // 학생 로그인
            itemDto.setSellerType(SellerType.STUDENT);
            itemDto.setSellerId(currentStudentId.get());
            itemDto.setSellerName(studentService.findNameById(currentStudentId.get()));
            Item item = itemMapper.toStudentSellerEntity(itemDto);
            Item savedItem = itemRepository.save(item);
            return itemMapper.toStudentSellerDto(savedItem);
        } else {
            throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
        }
    }

    // 상품 조회
    public List<ItemDTO> findItemList() {
        Optional<Long> currentTeacherId = teacherService.getOptionalCurrentTeacherId();
        Optional<Long> currentStudentId = studentService.getOptionalCurrentStudentId();

        Long teacherId = 0L;
        if (currentTeacherId.isPresent()) { // 교사 로그인
            teacherId = currentTeacherId.get();
        } else if (currentStudentId.isPresent()) { // 학생 로그인
            teacherId = studentService.findTeacherIdByStudentId(currentStudentId.get());
        } else {
            throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
        }

        return itemRepository.findItemsByTeacherId(teacherId)
                .stream()
                .filter(item -> item.getQuantity() > 0)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    // 특정 상품 상세 조회 - 존재하지 않으면 예외 발생
    public ItemDTO findItem(Long itemId) {
        return itemRepository.findById(itemId)
                .map(itemMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다. ID: " + itemId));
    }

    // 특정 학생/교사가 판매한 상품 조회
    public List<ItemDTO> findItemListBySeller() {
        Optional<Long> currentTeacherId = teacherService.getOptionalCurrentTeacherId();
        Optional<Long> currentStudentId = studentService.getOptionalCurrentStudentId();

        if (currentTeacherId.isPresent()) { // 교사 로그인
            return itemRepository.findBySellerTeacher_Id(currentTeacherId.get())
                    .stream()
                    .map(itemMapper::toDto)
                    .collect(Collectors.toList());
        } else if (currentStudentId.isPresent()) { // 학생 로그인
            return itemRepository.findBySellerStudent_Id(currentStudentId.get())
                    .stream()
                    .map(itemMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("현재 인증된 사용자를 찾을 수 없습니다.");
        }
    }

    // 특정 교사의 학급 내 상품 리스트 조회
//    public List<ItemDTO> findItemListByTeacher() {
//        Long teacherId = teacherService.getCurrentTeacherId();
//        return itemRepository.findItemsByTeacherId(teacherId)
//                .stream()
//                .map(itemMapper::toDto)
//                .collect(Collectors.toList());
//    }

    // 특정 교사의 학급 내 판매 중인 상품 리스트 조회
    public List<ItemDTO> findActiveItemListByTeacher() {
        Long teacherId = teacherService.getCurrentTeacherId();
        return itemRepository.findBySellerTeacher_Id(teacherId)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

}
