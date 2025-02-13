package com.ladysparks.ttaenggrang.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ladysparks.ttaenggrang.domain.notification.entity.Notification.NotificationStatus;
import com.ladysparks.ttaenggrang.domain.notification.entity.Notification.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"id", "status", "createdAt", "readAt"}, allowGetters = true)
public class NotificationDTO {

    private Long id;

    private Long senderStudentId;
    private Long senderTeacherId;
    private Long receiverStudentId;
    private Long receiverTeacherId;

    @NotNull(message = "토큰(targetToken)은 필수 항목입니다.")
    private String targetToken; // FCM 토큰

    @NotNull(message = "알림 타입(notificationType)은 필수 항목입니다.")
    private NotificationType notificationType;

    @NotNull(message = "제목(title)은 필수 항목입니다.")
    private String title;

    private String message;

    private NotificationStatus status;

    private Timestamp createdAt;

    private Timestamp readAt;

}