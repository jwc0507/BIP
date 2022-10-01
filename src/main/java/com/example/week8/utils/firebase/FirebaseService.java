//package com.example.week8.utils.firebase;
//
//
//import com.example.week8.dto.FCMMessageDto;
//import com.example.week8.dto.response.ResponseDto;
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
//    private final String CONFIG_PATH = "event-scheduler-4736e-firebase-adminsdk-1atyo-5249f2d5a7.json";
//    private final String AUTH_URL = "https://www.googleapis.com/auth/cloud-platform";
//    private final String SEND_URL = "https://fcm.googleapis.com/v1/projects/event-scheduler-4736e/messages:send";
//    private final ObjectMapper objectMapper;
//
//    public ResponseDto<?> sendMessageTo(String targetToken, String title, String body) throws IOException {
//        String message = makeMessage(targetToken, title, body);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken());
//
//        HttpEntity<Object> entity = new HttpEntity<>(message, headers);
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                SEND_URL,
//                HttpMethod.POST,
//                entity,
//                String.class
//        );
//        HttpStatus status = response.getStatusCode();
//        String responseBody = response.getBody();
//
//        if (status.equals(HttpStatus.OK)) { // 발송 API 호출 성공
//            return ResponseDto.success(responseBody);
//        }
//        return ResponseDto.fail(responseBody);
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
