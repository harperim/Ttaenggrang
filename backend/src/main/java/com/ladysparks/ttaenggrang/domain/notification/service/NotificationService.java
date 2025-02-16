package com.ladysparks.ttaenggrang.domain.notification.service;

import com.ladysparks.ttaenggrang.domain.notification.dto.NotificationDTO;
import com.ladysparks.ttaenggrang.domain.notification.dto.NotificationPersistanceDTO;
import com.ladysparks.ttaenggrang.domain.notification.entity.Notification;
import com.ladysparks.ttaenggrang.domain.notification.entity.Notification.NotificationStatus;
import com.ladysparks.ttaenggrang.domain.notification.entity.Notification.NotificationType;
import com.ladysparks.ttaenggrang.domain.notification.mapper.NotificationMapper;
import com.ladysparks.ttaenggrang.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final FCMWithDataService fcmWithDataService;

    /**
     * 뉴스
     */
    public void sendNewsNotificationToStudents(Long teacherId, String title, String content) throws IOException {
        String category = "NEWS";
        long time = System.currentTimeMillis();
        String sender = "System";
        String receiver = "STUDENT";

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .category(category)
                .title(title)
                .content(content)
                .time(time)
                .sender(sender)
                .receiver(receiver)
                .build();

        fcmWithDataService.broadCastToAllStudents(teacherId, notificationDTO);
    }

    /**
     * 📌 FCM 알림을 받은 후 DB에 저장하는 메서드
     */
    @Transactional
    public NotificationPersistanceDTO saveNotification(NotificationPersistanceDTO notificationPersistanceDTO) {
        validateNotification(notificationPersistanceDTO);
        Notification notification = notificationMapper.toEntity(notificationPersistanceDTO);
        return notificationMapper.toDto(notificationRepository.save(notification));
    }

    /**
     * 📌 특정 학생의 읽지 않은 알림 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationPersistanceDTO> getUnreadNotificationsForStudent(Long studentId) {
        return notificationRepository.findByReceiverStudentIdAndStatus(studentId, NotificationStatus.UNREAD)
                .stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 📌 특정 교사의 읽지 않은 알림 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationPersistanceDTO> getUnreadNotificationsForTeacher(Long teacherId) {
        return notificationRepository.findByReceiverTeacherIdAndStatus(teacherId, NotificationStatus.UNREAD)
                .stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 📌 알림 읽음 상태로 변경
     */
    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 알림을 찾을 수 없습니다."));
        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(Timestamp.from(Instant.now()));
        notificationRepository.save(notification);
    }

    private void validateNotification(NotificationPersistanceDTO notificationPersistanceDTO) {
        // 발신자(sender)는 반드시 존재해야 함 (Student 또는 Teacher 중 하나)
        if (notificationPersistanceDTO.getSenderStudentId() == null && notificationPersistanceDTO.getSenderTeacherId() == null) {
            throw new IllegalArgumentException("알림을 보낸 사람(sender_student_id 또는 sender_teacher_id) 중 하나는 반드시 존재해야 합니다.");
        }

        // 발신자는 두 개 동시에 설정될 수 없음
        if (notificationPersistanceDTO.getSenderStudentId() != null && notificationPersistanceDTO.getSenderTeacherId() != null) {
            throw new IllegalArgumentException("알림을 보낸 사람(sender_student_id 또는 sender_teacher_id)은 하나만 존재해야 합니다.");
        }

        // 수신자(receiver)는 반드시 존재해야 함 (Student 또는 Teacher 중 하나)
        if (notificationPersistanceDTO.getReceiverStudentId() == null && notificationPersistanceDTO.getReceiverTeacherId() == null) {
            throw new IllegalArgumentException("알림을 받는 사람(receiver_student_id 또는 receiver_teacher_id) 중 하나는 반드시 존재해야 합니다.");
        }

        // 수신자는 두 개 동시에 설정될 수 없음
        if (notificationPersistanceDTO.getReceiverStudentId() != null && notificationPersistanceDTO.getReceiverTeacherId() != null) {
            throw new IllegalArgumentException("알림을 받는 사람(receiver_student_id 또는 receiver_teacher_id)은 하나만 존재해야 합니다.");
        }

        // 발신자와 수신자가 같을 수 없음 (자기 자신에게 알림 전송 불가)
        if (Objects.equals(notificationPersistanceDTO.getSenderStudentId(), notificationPersistanceDTO.getReceiverStudentId())) {
            throw new IllegalArgumentException("자기 자신에게 알림을 보낼 수 없습니다. (sender_student_id와 receiver_student_id가 동일함)");
        }

        if (Objects.equals(notificationPersistanceDTO.getSenderTeacherId(), notificationPersistanceDTO.getReceiverTeacherId())) {
            throw new IllegalArgumentException("자기 자신에게 알림을 보낼 수 없습니다. (sender_teacher_id와 receiver_teacher_id가 동일함)");
        }

        // 특정 알림 타입에 따른 추가 검증 (예시)
        if (notificationPersistanceDTO.getNotificationType() == NotificationType.ITEM_SALE_REQUEST && notificationPersistanceDTO.getReceiverTeacherId() == null) {
            throw new IllegalArgumentException("ITEM_SALE_REQUEST 알림은 receiver_teacher_id가 필요합니다.");
        }
    }

}
