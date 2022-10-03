package com.example.week8.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class FriendSecondNameRequestDto {

    @NotNull
    private String friendNickname;

    @NotNull
    private String secondName;
}
