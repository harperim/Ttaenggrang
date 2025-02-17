package com.ladysparks.ttaenggrang.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.ladysparks.ttaenggrang.domain.notification.dto.FcmMessageWithData;
import com.ladysparks.ttaenggrang.domain.notification.dto.NotificationDTO;
import com.ladysparks.ttaenggrang.global.utill.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMWithDataService {

    private final ObjectMapper objectMapper;
    private final OkHttpClient client;

    /**
     * 📌 FCM AccessToken 가져오기
     * FCM에 push 요청을 보낼 때 인증을 위해 Header에 포함시킬 AccessToken 생성
     */
    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(Constants.FIREBASE_KEY_FILE).getInputStream())
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging")); // ✅ 올바른 권한 설정

        googleCredentials.refreshIfExpired();
        String token = googleCredentials.getAccessToken().getTokenValue();

        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("🔥 FCM Access Token을 가져오지 못했습니다!");
        }

        return token;
    }

    /**
     * 📌 FCM 메시지 전송 (비동기)
     */
    @Async("taskExecutor") // 비동기 실행
    public CompletableFuture<Integer> sendToStudent(String targetToken, NotificationDTO notificationDTO) throws IOException {
        if (targetToken != null && !targetToken.trim().isEmpty()) {  // ✅ 공백 및 개행 문자 제거
            String cleanedToken = targetToken.trim();  // ✅ FCM 토큰 정리
            log.info("📨 정리된 FCM 토큰: {}", cleanedToken);

            String message = makeDataMessage(cleanedToken, notificationDTO);
            sendDataMessageTo(message); // 비동기 호출
        }
        return CompletableFuture.completedFuture(1);
    }

    /**
     * 📌 FCM 메시지 전송 (비동기)
     */
    @Async("taskExecutor") // 비동기 실행
    public CompletableFuture<Integer> broadCastToAllStudents(List<String> targetTokens, NotificationDTO notificationDTO) throws IOException {
        for (String targetToken : targetTokens) {
            if (targetToken != null && !targetToken.trim().isEmpty()) {  // ✅ 공백 및 개행 문자 제거
                String cleanedToken = targetToken.trim();  // ✅ FCM 토큰 정리
                log.info("📨 정리된 FCM 토큰: {}", cleanedToken);

                String message = makeDataMessage(cleanedToken, notificationDTO);
                sendDataMessageTo(message); // 비동기 호출
            }
        }
        return CompletableFuture.completedFuture(targetTokens.size());
    }

    /**
     * 📌 FCM 메시지 생성 (Data 메시지 형식)
     */
    private String makeDataMessage(String targetToken, NotificationDTO notificationDTO) throws JsonProcessingException {
        Map<String, String> data = new HashMap<>();
        data.put("category", notificationDTO.getCategory());
        data.put("title", notificationDTO.getTitle());
        data.put("content", notificationDTO.getContent());
        data.put("sendTime", String.valueOf(notificationDTO.getTime()));
        data.put("receiver", notificationDTO.getReceiver());

        // FCM 메시지 포맷 (올바른 형식)
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", Map.of(
                "token", targetToken,  // FCM 디바이스 토큰
                "data", data                // 데이터 메시지
        ));

        return objectMapper.writeValueAsString(messageMap);
    }

    /**
     * 📌 FCM 메시지 전송 요청
     */
    private boolean sendDataMessageTo(String message) throws IOException {
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        String accessToken = getAccessToken();

        log.info("📨 FCM 요청: {}", message);
        log.info("🔑 FCM 인증 토큰: {}", accessToken);

        Request request = new Request.Builder()
                .url(Constants.API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)  // ✅ FCM 토큰 적용
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("❌ FCM 전송 실패! 응답 코드: {}, 응답 메시지: {}", response.code(), response.body().string());
                return false;
            } else {
                log.info("✅ FCM 전송 성공!");
                return true;
            }
        }
    }

}
