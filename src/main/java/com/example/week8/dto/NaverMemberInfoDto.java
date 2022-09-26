package com.example.week8.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class NaverMemberInfoDto {
    private String id;
    private String nickname;
    private String email;
    private String imageUrl;
    private String phoneNumber;
}
