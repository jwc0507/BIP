package com.example.week8.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FriendInfoResponseDto {
    private final String nickname;
    private final Long creditScore;//int로 정의하는게 맞지 않나?
}
