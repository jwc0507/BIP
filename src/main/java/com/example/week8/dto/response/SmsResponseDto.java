package com.example.week8.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class SmsResponseDto {
    String requestId;   // 요청 id
    LocalDateTime requestTime;  // 요청 시간
    String statusCode;  // 성공코드(202), 나머지는 http 규격따라 실패코드 반환
    String statusName;  // 요청 상태명 success or fail
}