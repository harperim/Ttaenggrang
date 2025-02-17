package com.ladysparks.ttaenggrang.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.ladysparks.ttaenggrang.domain.notification.dto.FcmMessageWithData;
import com.ladysparks.ttaenggrang.domain.notification.dto.NotificationDTO;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.global.utill.Constants;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * FCM 알림 메시지 생성
 * background 대응을 위해서 data로 전송한다.
 *
 * @author taeshik.heo
 *
 */
@Service
@RequiredArgsConstructor
public class FCMWithDataService {

    private static final Logger logger = LoggerFactory.getLogger(FCMWithDataService.class);

    private final ObjectMapper objectMapper;
    private final OkHttpClient client;

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
//    private String makeDataMessage(String targetToken, NotificationDTO notificationDTO) throws JsonProcessingException {
//        Map<String, String> data = new HashMap<>();
//        data.put("title", notificationDTO.getTitle());
//
//
//        FcmMessageWithData.Message message = new FcmMessageWithData.Message();
//        message.setToken(targetToken); // 🔥 타겟 디바이스 토큰
//        message.setData(data);
//
//        return objectMapper.writeValueAsString(new FcmMessageWithData(false, message));
//    }

    /**
     * 📌 FCM 메시지 전송 후 Notification 저장
     * targetToken에 해당하는 device로 FCM 푸시 알림 전송
     * background 대응을 위해서 data로 전송한다.
     */
//    public String sendDataMessageTo(String message) throws IOException {
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
//        Request request = new Request.Builder()
//                .url(Constants.API_URL)
//                .post(requestBody)
//                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
//                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
//                .build();
//
//        Response response = client.newCall(request).execute();
//        return response.body().string();
//    }

    // 클라이언트 토큰 관리
    public void addToken(String token) {
        Constants.clientTokens.add(token);
    }

    @Async("taskExecutor") // 비동기 실행 (taskExecutor 사용)
    public void sendToStudent(String targetToken, NotificationDTO notificationDTO) throws IOException {
        String message = makeDataMessage(targetToken, notificationDTO);
        sendDataMessageTo(message);
        CompletableFuture.completedFuture(1);
    }

    @Async("taskExecutor") // 비동기 실행 (taskExecutor 사용)
    public void broadCastToAllStudents(List<String> targetTokens, NotificationDTO notificationDTO) throws IOException {
        for (String targetToken : targetTokens) {
            String message = makeDataMessage(targetToken, notificationDTO);
            sendDataMessageTo(message); // 비동기 호출
        }
        CompletableFuture.completedFuture(targetTokens.size());
    }

    private String makeDataMessage(String targetToken, NotificationDTO notificationDTO) throws JsonProcessingException {
        Map<String, String> data = new HashMap<>();
        data.put("title", notificationDTO.getTitle());

        FcmMessageWithData.Message message = new FcmMessageWithData.Message();
        message.setToken(targetToken);
        message.setData(data);

        return objectMapper.writeValueAsString(new FcmMessageWithData(false, message));
    }

    private void sendDataMessageTo(String message) throws IOException {
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(Constants.API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        client.newCall(request).execute();
    }

}

