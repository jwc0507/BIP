package com.example.week8.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OauthLoginResponseDto {
    private String nickname;
    private String phoneNumber;
    private String email;
}
