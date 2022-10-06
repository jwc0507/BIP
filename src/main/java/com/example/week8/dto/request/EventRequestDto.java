package com.example.week8.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class EventRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String eventDateTime;

    @NotBlank
    private String place;

    @NotBlank
    private String coordinate;

    @NotBlank
    private String content;

    @NotNull
    private int point;

}
