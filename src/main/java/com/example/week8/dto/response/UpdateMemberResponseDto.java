package com.example.week8.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateMemberResponseDto {
    private String nickname;
    private String phoneNumber;
    private String email;
    private String profileImgUrl;
    private int point;
    private String creditScore;
    private int numOfDone;
}
