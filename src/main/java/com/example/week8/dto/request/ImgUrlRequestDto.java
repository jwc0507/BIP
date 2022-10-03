package com.example.week8.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class ImgUrlRequestDto {

    @NotNull
    private String imgUrl;
}
