package com.example.week8.dto;

import lombok.*;


import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class SmsRequestDto {
    String type;        // sms, lms, mms 종류
    String contentType; // comm : 일반, ad : 광고
    String countryCode; // 국가번호 (082)
    String from;    // 등록된 발신번호
    String content; // 기본 메세지 내용
    List<MessageDto> messages;  // to(수신번호), subject(개별 메세지 제목, LMS, MMS에서만 사용), content(개별메세지 내용 sms: 최대 80byte)
//    List<FileDto> files;    // MMS에서 사용하는 파일 보내기
//    String reserveTime;     // 메세지 발송 예약 일시 (yyyy-MM-dd-HH:mm), 예약발송용
//    String reserveTimeZone; // 예약 일시 타임존 (기본 seoul), 예약발송용
//    String scheduleCode;    // 등록 스케줄 코드, 예약발송용
}
