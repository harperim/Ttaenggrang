package com.ladysparks.ttaenggrang.domain.vote.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private VoteItem voteItem;
}
