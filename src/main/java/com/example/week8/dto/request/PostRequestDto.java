package com.example.week8.dto.request;

import com.example.week8.domain.enums.Board;
import com.example.week8.domain.enums.Category;
import lombok.Getter;

@Getter
public class PostRequestDto {
    private Board board;
    private Category category;
    private String title;
    private String content;
//    private String imgUrl;
    private String address;
    private String coordinate;
    private int point;
}
