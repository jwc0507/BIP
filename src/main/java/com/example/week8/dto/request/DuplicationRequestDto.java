package com.example.week8.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class DuplicationRequestDto {
    @NotBlank
    private String value;

}
