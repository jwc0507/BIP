package com.example.week8.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class MemberInfoRequestDto {

    @NotBlank
    private String value;
}
