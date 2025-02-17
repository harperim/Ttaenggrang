package com.ladysparks.ttaenggrang.domain.item.repository;

import com.ladysparks.ttaenggrang.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // 특정 교사가 담당하는 학생들이 판매한 아이템 조회
    @Query("SELECT i FROM Item i " +
            "WHERE (i.sellerStudent.id = :teacherId AND i.sellerType = 'STUDENT') " +
            "OR (i.sellerTeacher.id = :teacherId AND i.sellerType = 'TEACHER')")    List<Item> findItemsByTeacherId(@Param("teacherId") Long teacherId);
    // 학생 판매자의 ID로 아이템 조회
    List<Item> findBySellerStudent_Id(Long sellerId);

    // 교사 판매자의 ID로 아이템 조회
    List<Item> findBySellerTeacher_Id(Long sellerId);

    @Query("SELECT i FROM Item i WHERE i.sellerStudent.teacher.id = :teacherId AND i.quantity > 0")
    List<Item> findActiveItemsByTeacherId(@Param("teacherId") Long teacherId);

}