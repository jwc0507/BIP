package com.example.week8.dto.request;

import com.example.week8.utils.customvalidation.ConvPointCheck;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ConversionPointToCreditDto {
    @ConvPointCheck
    private int point;
    @NotBlank
    private String nickname;
}
