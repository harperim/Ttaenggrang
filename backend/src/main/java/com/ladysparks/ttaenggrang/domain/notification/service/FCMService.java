package com.ladysparks.ttaenggrang.domain.notification.service;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;

import com.ladysparks.ttaenggrang.domain.notification.dto.BroadcastNotificationDTO;
import com.ladysparks.ttaenggrang.domain.notification.dto.FcmMessage;
import com.ladysparks.ttaenggrang.domain.notification.dto.NotificationPersistanceDTO;
import com.ladysparks.ttaenggrang.domain.student.dto.StudentResponseDTO;
import com.ladysparks.ttaenggrang.domain.student.service.StudentService;
import com.ladysparks.ttaenggrang.domain.teacher.service.TeacherService;
import com.ladysparks.ttaenggrang.global.utill.Constants;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

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
 *
 * @author taeshik.heo
 *
 */
@Service
public class FCMService {

    private static final Logger logger = LoggerFactory.getLogger(FCMService.class);

    public final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final TeacherService teacherService;
    private final StudentService studentService;

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
    private String makeMessage(String title, String body, String targetToken) throws JsonProcessingException {
        FcmMessage.Notification noti = new FcmMessage.Notification(title, body, null);
        FcmMessage.Message message = new FcmMessage.Message(noti, targetToken);
        FcmMessage fcmMessage = new FcmMessage(false, message);

        return objectMapper.writeValueAsString(fcmMessage); // JSON 문자열로 변환(직렬화)
    }

    private String makeMessage(NotificationPersistanceDTO notificationPersistanceDTO) throws JsonProcessingException {
        String title = notificationPersistanceDTO.getTitle();
        String body = notificationPersistanceDTO.getMessage();
        String targetToken = notificationPersistanceDTO.getTargetToken();

        FcmMessage.Notification noti = new FcmMessage.Notification(title, body, null);
        FcmMessage.Message message = new FcmMessage.Message(noti, targetToken);
        FcmMessage fcmMessage = new FcmMessage(false, message);

        return objectMapper.writeValueAsString(fcmMessage); // JSON 문자열로 변환(직렬화)
    }

    /**
     * targetToken에 해당하는 device로 FCM 푸시 알림 전송
     */
    public String sendMessageTo(String message) throws IOException {
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

        return response.body().string();
    }

    public NotificationPersistanceDTO sendMessageTo(NotificationPersistanceDTO notificationPersistanceDTO) throws IOException {
        // Notification 테이블에 저장 후 FCM 메시지를 전송
        NotificationPersistanceDTO savedNotificationPersistanceDTO = notificationService.saveNotification(notificationPersistanceDTO);

        String message = makeMessage(savedNotificationPersistanceDTO);
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

        return savedNotificationPersistanceDTO;
    }

    public FCMService(ObjectMapper objectMapper, NotificationService notificationService, TeacherService teacherService, StudentService studentService){
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
        this.teacherService = teacherService;
        this.studentService = studentService;
    }

    // 클라이언트 토큰 관리
    public void addToken(String token) {
        if(!Constants.clientTokens.contains(token)) {
            Constants.clientTokens.add(token);
        }
    }

    // 등록된 모든 토큰을 이용해서 broadcasting
    public int broadCastToAllStudents(Long teacherId, String title, String body) throws IOException {
        List<StudentResponseDTO> studentResponseDTOList = studentService.findAllByTeacherId(teacherId);
        for(StudentResponseDTO student: studentResponseDTOList) {
            String targetToken = studentService.findFCMTokenById(student.getId());
            String message = makeMessage(title, body, targetToken);
            String response = sendMessageTo(message);
        }
        return studentResponseDTOList.size();
    }

    public int broadCastMessage(BroadcastNotificationDTO broadcastNotificationDTO) throws IOException {
        for(String token: Constants.clientTokens) {
            // Notification 테이블에 저장 후 FCM 메시지를 전송
            NotificationPersistanceDTO notificationPersistanceDTO = NotificationPersistanceDTO.builder()
                    .senderTeacherId(broadcastNotificationDTO.getTeacherId())
                    .notificationType(broadcastNotificationDTO.getNotificationType())
                    .title(broadcastNotificationDTO.getTitle())
                    .message(broadcastNotificationDTO.getMessage())
                    .status(broadcastNotificationDTO.getStatus())
                    .build();
            NotificationPersistanceDTO savedNotificationPersistanceDTO = notificationService.saveNotification(notificationPersistanceDTO);

            String message = makeMessage(savedNotificationPersistanceDTO);
            logger.info("📨 FCM Message: {}", message);

            sendMessageTo(notificationPersistanceDTO);
        }
        return Constants.clientTokens.size();
    }

}