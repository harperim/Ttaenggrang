package com.ladysparks.ttaenggrang.domain.news.entity;

import com.ladysparks.ttaenggrang.domain.stock.entity.Stock;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    private Timestamp createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "news_type", length = 50)
    private NewsType newsType;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
}
