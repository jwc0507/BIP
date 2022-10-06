package com.example.week8.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
public class FriendAdditionRequestDto {
    @NotBlank
    private  String value;
}
