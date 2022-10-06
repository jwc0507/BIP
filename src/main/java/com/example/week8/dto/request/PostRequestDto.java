package com.example.week8.dto.request;

import com.example.week8.domain.enums.Board;
import com.example.week8.domain.enums.Category;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class PostRequestDto {

    @NotNull
    private Board board;

    @NotNull
    private Category category;

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
