package com.ladysparks.ttaenggrang.domain.notification.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.ladysparks.ttaenggrang.domain.notification.dto.BroadcastNotificationDTO;
import com.ladysparks.ttaenggrang.domain.notification.dto.FcmMessageWithData;
import com.ladysparks.ttaenggrang.domain.notification.dto.NotificationDTO;
import com.ladysparks.ttaenggrang.domain.notification.entity.NotificationType;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.global.utill.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

/**
 * FCM 알림 메시지 생성
 * background 대응을 위해서 data로 전송한다.
 *
 * @author taeshik.heo
 *
 */
@Service
@RequiredArgsConstructor
public class FirebaseCloudMessageWithDataService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseCloudMessageWithDataService.class);

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final StudentService studentService;
    private final TeacherService teacherService;

    /**
     * 📌 FCM AccessToken 가져오기
     * FCM에 push 요청을 보낼 때 인증을 위해 Header에 포함시킬 AccessToken 생성
     */
    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(Constants.FIREBASE_KEY_FILE).getInputStream())
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    /**
     * 📌 FCM 메시지 생성 (DTO 활용)
     * background 대응을 위해서 data로 전송한다.
     */
    private String makeDataMessage(NotificationDTO notificationDTO) throws JsonProcessingException {
        Map<String, String> data = new HashMap<>();
        data.put("myTitle", notificationDTO.getTitle());
        data.put("myBody", notificationDTO.getMessage());

        FcmMessageWithData.Message message = new FcmMessageWithData.Message();
        message.setToken(notificationDTO.getTargetToken()); // 🔥 타겟 디바이스 토큰
        message.setData(data);

        return objectMapper.writeValueAsString(new FcmMessageWithData(false, message));
    }

    /**
     * 📌 FCM 메시지 전송 후 Notification 저장
     * targetToken에 해당하는 device로 FCM 푸시 알림 전송
     * background 대응을 위해서 data로 전송한다.
     */
    public NotificationDTO sendDataMessageTo(NotificationDTO notificationDTO) throws IOException {
        // Notification 테이블에 저장 루 FCM 메시지를 전송
        NotificationDTO savedNotificationDTO = notificationService.saveNotification(notificationDTO);

        String message = makeDataMessage(savedNotificationDTO);
        logger.info("📨 FCM Message: {}", message);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(Constants.API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();
        logger.info("📨 FCM Response: {}", response.body().string());

        return savedNotificationDTO;
    }


    // 클라이언트 토큰 관리
    public void addToken(String token) {
        Constants.clientTokens.add(token);
    }

    // 등록된 모든 토큰을 이용해서 broadcasting
    public int broadCastDataMessage(BroadcastNotificationDTO broadcastNotificationDTO) throws IOException {
        for(String token: Constants.clientTokens) {
            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .senderTeacherId(broadcastNotificationDTO.getTeacherId())
                    .targetToken(token)
                    .notificationType(broadcastNotificationDTO.getNotificationType())
                    .title(broadcastNotificationDTO.getTitle())
                    .message(broadcastNotificationDTO.getMessage())
                    .status(broadcastNotificationDTO.getStatus())
                    .build();
            String message = makeDataMessage(notificationDTO);
            logger.info("📨 FCM Message: {}", message);

            sendDataMessageTo(notificationDTO);
        }
        return Constants.clientTokens.size();
    }

}

