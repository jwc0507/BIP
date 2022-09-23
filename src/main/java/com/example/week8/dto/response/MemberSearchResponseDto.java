package com.example.week8.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberSearchResponseDto {
    private Long id;
    private String nickname;
    private String profileImgUrl;
    private double creditScore;
}
