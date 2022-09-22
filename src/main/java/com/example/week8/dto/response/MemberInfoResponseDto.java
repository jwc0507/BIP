package com.example.week8.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberInfoResponseDto {
    private String nickname;
    private String phoneNumber;
    private String email;
    private String profileUrlImg;
    private int point;
    private double creditScore;
    private int numOfDone;
}
