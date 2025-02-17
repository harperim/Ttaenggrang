package com.ladysparks.ttaenggrang.domain.teacher.repository;

import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByEmail(String email);

    String findNameById(Long teacherId);

    @Modifying
    @Transactional
    @Query("UPDATE Teacher t SET t.fcmToken = :fcmToken WHERE t.id = :teacherId")
    int updateFcmToken(@Param("teacherId") Long teacherId, @Param("fcmToken") String fcmToken);

}
