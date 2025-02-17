package com.ladysparks.ttaenggrang.domain.notification.repository;

import com.ladysparks.ttaenggrang.domain.notification.entity.Notification;
import com.ladysparks.ttaenggrang.domain.notification.entity.Notification.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverStudentIdAndStatus(Long receiverStudentId, NotificationStatus status);

    List<Notification> findByReceiverTeacherIdAndStatus(Long receiverTeacherId, NotificationStatus status);

}
