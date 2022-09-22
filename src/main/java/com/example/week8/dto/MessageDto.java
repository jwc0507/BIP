package com.example.week8.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MessageDto {
    String to;  // 수신번호 -제외해야함
//    String subject; // lms, mms에서만 사용하므로 주석
    String content; // 최대 80byte사용 (sms)
}