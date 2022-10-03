package com.example.week8.dto.request;

import com.example.week8.domain.enums.Board;
import com.example.week8.domain.enums.Category;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class PostRequestDto {

    @NotBlank
    private Board board;

    @NotBlank
    private Category category;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

//    @NotNull
//    private String imgUrl;

    @NotBlank
    private String address;

    @NotBlank
    private String coordinate;

    @NotBlank
    private String point;
}
