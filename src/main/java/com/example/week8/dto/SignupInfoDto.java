package com.example.week8.dto;

import com.example.week8.domain.enums.Authority;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupInfoDto {
    String email;
    String phoneNumber;
    String imgUrl;
    String naverId;
    Long kakaoId;
    Authority role;
}
