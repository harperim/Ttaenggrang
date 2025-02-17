package com.ladysparks.ttaenggrang.domain.notification.entity;

import com.ladysparks.ttaenggrang.domain.student.entity.Student;
import com.ladysparks.ttaenggrang.domain.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_student_id")
    private Student senderStudent;

    @ManyToOne
    @JoinColumn(name = "sender_teacher_id")
    private Teacher senderTeacher;

    @ManyToOne
    @JoinColumn(name = "receiver_student_id")
    private Student receiverStudent;

    @ManyToOne
    @JoinColumn(name = "receiver_teacher_id")
    private Teacher receiverTeacher;

    @Column(nullable = false)
    private String targetToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp readAt;

    @PrePersist
    protected void prePersist() { // 데이터가 INSERT 되기 전에 기본값 설정
        if (this.status == null) {
            this.status = NotificationStatus.UNREAD;
        }
    }

    public enum NotificationStatus {
        UNREAD,
        READ,
        FAILED
    }

    public enum NotificationType {
        ITEM_SALE_REQUEST,
        ITEM_SALE_REJECTION,
        ITEM_USAGE_REQUEST,
        ITEM_USAGE_REJECTION,
        FAKE_NEWS,
        SAVINGS_MISSED,
        SALARY_PAID,
        SAVINGS_MATURED,
        VOTE_RESULT,
        WEEKLY_REPORT
    }

}
