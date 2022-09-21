package com.example.week8.dto.request;

import lombok.Getter;

@Getter
public class EmailLoginRequestDto {
    // 이메일로 로그인하기 위한 인증코드, 이메일 주소를 받음
    private String authCode;
    private String email;
}
