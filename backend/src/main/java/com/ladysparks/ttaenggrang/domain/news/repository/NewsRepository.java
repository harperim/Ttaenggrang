package com.ladysparks.ttaenggrang.domain.news.repository;

import com.ladysparks.ttaenggrang.domain.news.entity.News;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    // 현재 로그인한 교사ID 기준, 뉴스 목록 조회
    @Query("SELECT n FROM News n WHERE n.teacher.id = :teacherId ORDER BY n.createdAt DESC")
    List<News> findByTeacherId(@Param("teacherId") Long teacherId);

}
