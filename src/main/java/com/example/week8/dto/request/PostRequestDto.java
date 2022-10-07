package com.example.week8.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class PostRequestDto {

    @NotBlank
    private String board;

    @NotBlank
    private String category;

    @NotBlank
    private String content;

    @NotNull
    private String[] imgUrlList;

    @NotBlank
    private String address;

    @NotBlank
    private String coordinate;

    @NotBlank
    private String point;
}
