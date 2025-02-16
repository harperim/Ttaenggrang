package com.ladysparks.ttaenggrang.global.docs.home;

import com.ladysparks.ttaenggrang.domain.notification.dto.BroadcastNotificationDTO;
import com.ladysparks.ttaenggrang.domain.notification.dto.NotificationPersistanceDTO;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Tag(name = "[교사/학생] 알림", description = "FCM 알림 관련 API")
public interface NotificationApiSpecification {

//    @Operation(summary = "(교사/학생) 개별 메시지 전송", description = """
//            💡 특정 사용자의 FCM 토큰을 이용하여 기본 알림 메시지를 전송합니다.
//
//            ---
//
//            **[ 요청 필드 ]**
//            - **senderStudentId** : 발신 학생 ID (선택)
//            - **senderTeacherId** : 발신 교사 ID (선택)
//            - **receiverStudentId** : 수신 학생 ID (선택)
//            - **receiverTeacherId** : 수신 교사 ID (선택)
//            - **targetToken** : FCM 토큰 (필수)
//            - **notificationType** : 알림 타입 (필수)
//            - **title** : 알림 제목 (필수)
//            - **message** : 알림 내용 (선택)
//
//            ---
//
//            **[ 설명 ]**
//            - 발신자는 `senderStudentId` 또는 `senderTeacherId` 중 **하나만 존재해야 합니다.** (둘 다 존재하거나 둘 다 null이면 안 됨)
//            - 수신자는 `receiverStudentId` 또는 `receiverTeacherId` 중 **하나만 존재해야 합니다.** (둘 다 존재하거나 둘 다 null이면 안 됨)
//            - **발신자와 수신자가 같을 수 없습니다.** (`senderStudentId == receiverStudentId` 또는 `senderTeacherId == receiverTeacherId` 금지)
//            """)
//    ResponseEntity<ApiResponse<NotificationPersistanceDTO>> sendMessageTo(@RequestBody @Valid NotificationPersistanceDTO notificationPersistanceDTO) throws IOException;
//
//    @Operation(summary = "(교사/학생) 개별 데이터 메시지 전송", description = """
//            💡 특정 사용자의 FCM 토큰을 이용하여 데이터 메시지를 전송합니다.
//
//            ---
//
//            **[ 요청 필드 ]**
//            - **senderStudentId** : 발신 학생 ID (선택)
//            - **senderTeacherId** : 발신 교사 ID (선택)
//            - **receiverStudentId** : 수신 학생 ID (선택)
//            - **receiverTeacherId** : 수신 교사 ID (선택)
//            - **targetToken** : FCM 토큰 (필수)
//            - **notificationType** : 알림 타입 (필수)
//            - **title** : 알림 제목 (필수)
//            - **message** : 알림 내용 (선택)
//
//            ---
//
//            **[ 설명 ]**
//            - 발신자는 `senderStudentId` 또는 `senderTeacherId` 중 **하나만 존재해야 합니다.** (둘 다 존재하거나 둘 다 null이면 안 됨)
//            - 수신자는 `receiverStudentId` 또는 `receiverTeacherId` 중 **하나만 존재해야 합니다.** (둘 다 존재하거나 둘 다 null이면 안 됨)
//            - **발신자와 수신자가 같을 수 없습니다.** (`senderStudentId == receiverStudentId` 또는 `senderTeacherId == receiverTeacherId` 금지)
//            - **백그라운드**에서 데이터 처리를 위해 활용될 수 있습니다.
//            """)
//    ResponseEntity<ApiResponse<NotificationPersistanceDTO>> sendDataMessageTo(@RequestBody @Valid NotificationPersistanceDTO notificationPersistanceDTO) throws IOException;
//
//    @Operation(summary = "(교사/학생) FCM 토큰 등록", description = """
//            💡 사용자의 FCM 토큰을 서버에 등록합니다.
//
//            ---
//
//            **[ 요청 필드 ]**
//            - **targetToken** : FCM 토큰 (필수)
//            """)
//    ResponseEntity<ApiResponse<String>> registerToken(@RequestParam String token);
//
//    @Operation(summary = "(교사) 전체 사용자 메시지 Broadcast", description = """
//            💡 모든 사용자에게 기본 알림 메시지를 전송합니다.
//
//            ---
//
//            **[ 요청 필드 ]**
//            - **notificationType** : 알림 타입 (필수)
//            - **title** : 알림 제목 (필수)
//            - **message** : 알림 내용 (선택)
//
//            ---
//
//            **[ 설명 ]**
//            - 서버에 저장된 토큰을 이용해 전체 학생들에게 알림을 전송합니다.
//            """)
//    ResponseEntity<ApiResponse<String>> broadcast(@RequestBody @Valid BroadcastNotificationDTO broadcastNotificationDTO) throws IOException;
//
//    @Operation(summary = "(교사) 전체 사용자 데이터 메시지 Broadcast", description = """
//            💡 모든 사용자에게 데이터 메시지를 전송합니다.
//
//            ---
//
//            **[ 요청 필드 ]**
//            - **notificationType** : 알림 타입 (필수)
//            - **title** : 알림 제목 (필수)
//            - **message** : 알림 내용 (선택)
//
//            ---
//
//            **[ 설명 ]**
//            - 서버에 저장된 토큰을 이용해 전체 학생들에게 알림을 전송합니다.
//            - **백그라운드**에서 데이터 처리를 위해 활용될 수 있습니다.
//            """)
//    ResponseEntity<ApiResponse<String>> broadcastData(@RequestBody @Valid BroadcastNotificationDTO broadcastNotificationDTO) throws IOException;

}
