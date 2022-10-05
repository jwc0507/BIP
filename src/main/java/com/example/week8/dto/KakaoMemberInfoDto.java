package com.example.week8.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class KakaoMemberInfoDto {
    private Long id;
    private String email;
    private String imageUrl;
}
