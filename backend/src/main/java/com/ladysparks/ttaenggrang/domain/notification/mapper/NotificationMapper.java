package com.ladysparks.ttaenggrang.domain.notification.mapper;

import com.ladysparks.ttaenggrang.domain.notification.dto.NotificationPersistanceDTO;
import com.ladysparks.ttaenggrang.domain.notification.entity.Notification;
import com.ladysparks.ttaenggrang.global.config.MapStructConfig;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface NotificationMapper {

    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    @Mapping(source = "senderStudent.id", target = "senderStudentId")
    @Mapping(source = "senderTeacher.id", target = "senderTeacherId")
    @Mapping(source = "receiverStudent.id", target = "receiverStudentId")
    @Mapping(source = "receiverTeacher.id", target = "receiverTeacherId")
    NotificationPersistanceDTO toDto(Notification notification);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senderStudent", expression = "java(notificationPersistanceDTO.getSenderStudentId() != null ? new Student(notificationPersistanceDTO.getSenderStudentId()) : null)")
    @Mapping(target = "senderTeacher", expression = "java(notificationPersistanceDTO.getSenderTeacherId() != null ? new Teacher(notificationPersistanceDTO.getSenderTeacherId()) : null)")
    @Mapping(target = "receiverStudent", expression = "java(notificationPersistanceDTO.getReceiverStudentId() != null ? new Student(notificationPersistanceDTO.getReceiverStudentId()) : null)")
    @Mapping(target = "receiverTeacher", expression = "java(notificationPersistanceDTO.getReceiverTeacherId() != null ? new Teacher(notificationPersistanceDTO.getReceiverTeacherId()) : null)")
    Notification toEntity(NotificationPersistanceDTO notificationPersistanceDTO);

}
