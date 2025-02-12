package com.ladysparks.ttaenggrang.domain.notification.service;

import java.io.IOException;

import java.util.Arrays;

import com.ladysparks.ttaenggrang.domain.notification.dto.BroadcastNotificationDTO;
import com.ladysparks.ttaenggrang.domain.notification.dto.FcmMessage;
import com.ladysparks.ttaenggrang.domain.notification.dto.NotificationDTO;
import com.ladysparks.ttaenggrang.global.utill.Constants;
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

/**
 * FCM 알림 메시지 생성
 *
 * @author taeshik.heo
 *
 */
@Component
public class FirebaseCloudMessageService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseCloudMessageService.class);

    public final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    /**
     * FCM에 push 요청을 보낼 때 인증을 위해 Header에 포함시킬 AccessToken 생성
     */
    private String getAccessToken() throws IOException {
        // GoogleApi를 사용하기 위해 oAuth2를 이용해 인증한 대상을 나타내는객체
        GoogleCredentials googleCredentials = GoogleCredentials
                // 서버로부터 받은 service key 파일 활용
                .fromStream(new ClassPathResource(Constants.FIREBASE_KEY_FILE).getInputStream())
                // 인증하는 서버에서 필요로 하는 권한 지정
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        String token = googleCredentials.getAccessToken().getTokenValue();

        return token;
    }

    /**
     * FCM 알림 메시지 생성
     */
    private String makeMessage(NotificationDTO notificationDTO) throws JsonProcessingException {
        String title = notificationDTO.getTitle();
        String body = notificationDTO.getMessage();
        String targetToken = notificationDTO.getTargetToken();

        FcmMessage.Notification noti = new FcmMessage.Notification(title, body, null);
        FcmMessage.Message message = new FcmMessage.Message(noti, targetToken);
        FcmMessage fcmMessage = new FcmMessage(false, message);

        return objectMapper.writeValueAsString(fcmMessage); // JSON 문자열로 변환(직렬화)
    }

    /**
     * targetToken에 해당하는 device로 FCM 푸시 알림 전송
     */
    public NotificationDTO sendMessageTo(NotificationDTO notificationDTO) throws IOException {
        // Notification 테이블에 저장 루 FCM 메시지를 전송
        NotificationDTO savedNotificationDTO = notificationService.saveNotification(notificationDTO);

        String message = makeMessage(savedNotificationDTO);
        logger.info("📨 FCM Message: {}", message);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(Constants.API_URL)
                .post(requestBody)
                // 전송 토큰 추가
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute(); // 응답 올 때까지 대기

        System.out.println(response.body().string());
//        logger.info("message : {}", message);

        return savedNotificationDTO;
    }

    public FirebaseCloudMessageService(ObjectMapper objectMapper, NotificationService notificationService){
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    // 클라이언트 토큰 관리
    public void addToken(String token) {
        if(!Constants.clientTokens.contains(token)) {
            Constants.clientTokens.add(token);
        }
    }

    // 등록된 모든 토큰을 이용해서 broadcasting
    public int broadCastMessage(BroadcastNotificationDTO broadcastNotificationDTO) throws IOException {
        for(String token: Constants.clientTokens) {
            // Notification 테이블에 저장 루 FCM 메시지를 전송
            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .senderTeacherId(broadcastNotificationDTO.getTeacherId())
                    .notificationType(broadcastNotificationDTO.getNotificationType())
                    .title(broadcastNotificationDTO.getTitle())
                    .message(broadcastNotificationDTO.getMessage())
                    .status(broadcastNotificationDTO.getStatus())
                    .build();
            NotificationDTO savedNotificationDTO = notificationService.saveNotification(notificationDTO);

            String message = makeMessage(savedNotificationDTO);
            logger.info("📨 FCM Message: {}", message);

            sendMessageTo(notificationDTO);
        }
        return Constants.clientTokens.size();
    }

}