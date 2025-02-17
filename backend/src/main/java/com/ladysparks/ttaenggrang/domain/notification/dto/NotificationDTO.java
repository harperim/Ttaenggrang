package com.ladysparks.ttaenggrang.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private String category;  // Report, News, Bank, OTHER....
    private String title;
    private String content;
    private Long time;        // 수신 시간
    private String sender;    // 발신자 정보
    private String receiver;  // TEACHER, STUDENT

}