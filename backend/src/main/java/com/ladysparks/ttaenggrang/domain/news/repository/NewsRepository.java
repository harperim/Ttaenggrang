package com.ladysparks.ttaenggrang.domain.news.repository;

import com.ladysparks.ttaenggrang.domain.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
}
