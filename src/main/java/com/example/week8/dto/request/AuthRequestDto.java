package com.example.week8.dto.request;

import lombok.Getter;

@Getter
public class AuthRequestDto {
    // 인증을 위한 이메일, 전화번호를 받음
    private String value;
}
