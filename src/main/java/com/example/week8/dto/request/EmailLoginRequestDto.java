package com.example.week8.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class EmailLoginRequestDto {
    // 이메일로 로그인하기 위한 인증코드, 이메일 주소를 받음
    @NotBlank
    private String authCode;
    @NotBlank
    private String email;
}
