package com.ladysparks.ttaenggrang.domain.notification.controller;

import com.ladysparks.ttaenggrang.domain.notification.dto.BroadcastNotificationDTO;
import com.ladysparks.ttaenggrang.domain.notification.dto.NotificationPersistanceDTO;
import com.ladysparks.ttaenggrang.domain.notification.service.FCMService;
import com.ladysparks.ttaenggrang.domain.notification.service.FCMWithDataService;
import com.ladysparks.ttaenggrang.global.docs.home.NotificationApiSpecification;
import com.ladysparks.ttaenggrang.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/notifications")
public class NotificationController implements NotificationApiSpecification {

//    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
//
//    private final FCMService service;
//    private final FCMWithDataService serviceWithData;
//
//    /**
//     * 📌 개별 메시지 전송 (기본 메시지)
//     */
//    @PostMapping("/send")
//    public ResponseEntity<ApiResponse<NotificationPersistanceDTO>> sendMessageTo(@RequestBody @Valid NotificationPersistanceDTO notificationPersistanceDTO) throws IOException {
//        logger.info("📨 sendMessageTo: token={}, title={}, body={}",
//                notificationPersistanceDTO.getTargetToken(), notificationPersistanceDTO.getTitle(), notificationPersistanceDTO.getMessage());
//        return ResponseEntity.ok(ApiResponse.success(service.sendMessageTo(notificationPersistanceDTO)));
//    }
//
//    /**
//     * 📌 개별 데이터 메시지 전송 (Background 처리 가능)
//     */
//    @PostMapping("/send-data")
//    public ResponseEntity<ApiResponse<NotificationPersistanceDTO>> sendDataMessageTo(@RequestBody @Valid NotificationPersistanceDTO notificationPersistanceDTO) throws IOException {
//        logger.info("📨 sendDataMessageTo: token={}, title={}, body={}",
//                notificationPersistanceDTO.getTargetToken(), notificationPersistanceDTO.getTitle(), notificationPersistanceDTO.getMessage());
//        return ResponseEntity.ok(ApiResponse.success(serviceWithData.sendDataMessageTo(notificationPersistanceDTO)));
//    }
//
//    /**
//     * 📌 클라이언트 FCM 토큰 등록
//     */
//    @PostMapping("/token")
//    public ResponseEntity<ApiResponse<String>> registerToken(@RequestParam String token) {
//        logger.info("📌 registerToken: token={}", token);
//        service.addToken(token);
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("'" + token + "' 등록 완료되었습니다."));
//    }
//
//    /**
//     * 📌 전체 사용자에게 메시지 Broadcast (기본 메시지)
//     */
//    @PostMapping("/broadcast")
//    public ResponseEntity<ApiResponse<String>> broadcast(@RequestBody @Valid BroadcastNotificationDTO broadcastNotificationDTO) throws IOException {
//        logger.info("📢 broadcast: title={}, body={}", broadcastNotificationDTO.getTitle(), broadcastNotificationDTO.getMessage());
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(getMessage(service.broadCastMessage(broadcastNotificationDTO))));
//    }
//
//    /**
//     * 📌 전체 사용자에게 데이터 메시지 Broadcast (Background 지원)
//     */
//    @PostMapping("/broadcast-data")
//    public ResponseEntity<ApiResponse<String>> broadcastData(@RequestBody @Valid BroadcastNotificationDTO broadcastNotificationDTO) throws IOException {
//        logger.info("📢 broadcast-data: title={}, body={}", broadcastNotificationDTO.getTitle(), broadcastNotificationDTO.getMessage());
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(getMessage(serviceWithData.broadCastDataMessage(broadcastNotificationDTO))));
//    }
//
//    /**
//     * 📌 메시지 결과 반환 (전송 성공 여부)
//     */
//    private String getMessage(int count) {
//        if (count > 0) {
//            return "✅ 메시지가 성공적으로 전송되었습니다.";
//        } else {
//            return "⚠️ 전송할 대상이 없거나 메시지 전송에 실패했습니다.";
//        }
//    }

}

