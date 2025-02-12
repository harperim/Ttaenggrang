package com.ladysparks.ttaenggrang.domain.teacher.repository;

import com.ladysparks.ttaenggrang.domain.teacher.entity.Nation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import javax.net.ssl.SSLSession;
import java.util.Optional;

public interface NationRepository extends JpaRepository<Nation, Long> {

    Optional<Nation> findByTeacher_Id(Long teacherId);

    boolean existsByTeacherId(Long teacherId);

}
