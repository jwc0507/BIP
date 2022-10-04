package com.example.week8.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ConversionPointToCreditDto {
    @NotBlank
    private int point;
    @NotBlank
    private String nickname;
}
