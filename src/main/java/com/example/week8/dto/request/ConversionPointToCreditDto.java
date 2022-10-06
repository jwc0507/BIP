package com.example.week8.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class ConversionPointToCreditDto {
    @NotNull
    private int point;
    @NotBlank
    private String nickname;
}
