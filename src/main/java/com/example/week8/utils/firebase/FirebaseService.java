//package com.example.week8.utils.firebase;
//
//import com.example.week8.dto.FCMMessageDto;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.auth.oauth2.GoogleCredentials;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class FirebaseService {
//    private final RestTemplate restTemplate; // 비공개 키 경로
//
//    private final String CONFIG_PATH = "firebase/firebase-key.json"; // 토큰 발급 URL
//    private final String AUTH_URL = "https://www.googleapis.com/auth/cloud-platform"; // 엔드포인트 URL
//    private final String SEND_URL = "https://fcm.googleapis.com/v1/projects/event-scheduler-4736e/messages:send";
//    private final ObjectMapper objectMapper;
//
//    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
//        String message = makeMessage(targetToken, title, body);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken());
//
//        final HttpEntity<Object> entity = new HttpEntity<>(message, headers);
//        final ResponseEntity<String> response = restTemplate.exchange(SEND_URL, HttpMethod.POST, entity, String.class);
//        final HttpStatus status = response.getStatusCode();
//        final String responseBody = response.getBody();
//
//        if (status.equals(HttpStatus.OK)) { // 발송 API 호출 성공
//
//        }
//        else { // 발송 API 호출 실패
//
//        }
//    }
//
//    // 파라미터를 FCM이 요구하는 body 형태로 만들어준다.
//    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
//        FCMMessageDto fcmMessage = FCMMessageDto.builder()
//                .message(FCMMessageDto.Message.builder()
//                        .token(targetToken)
//                        .notification(FCMMessageDto.Notification.builder()
//                                .title(title)
//                                .body(body)
//                                .image(null)
//                                .build()
//                        )
//                        .build()
//                )
//                .validate_only(false)
//                .build();
//        return objectMapper.writeValueAsString(fcmMessage);
//    }
//
//    // 토큰발급
//    public String getAccessToken() throws IOException {
//        GoogleCredentials googleCredentials = GoogleCredentials
//                .fromStream(new ClassPathResource(CONFIG_PATH).getInputStream())
//                .createScoped(List.of(AUTH_URL));
//        googleCredentials.refreshIfExpired();
//        return googleCredentials.getAccessToken().getTokenValue();
//    }
//
//}
