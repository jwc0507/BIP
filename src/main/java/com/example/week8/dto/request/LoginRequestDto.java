package com.example.week8.dto.request;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    // 로그인, 회원가입 하기 위한 인증코드, 전화번호를 받음
    private String authCode;
    private String phoneNumber;
}
