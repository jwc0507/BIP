package com.example.week8.dto.request;

import com.example.week8.domain.enums.DivisionOne;
import com.example.week8.domain.enums.DivisionTwo;
import lombok.Getter;

@Getter
public class PostRequestDto {
    private DivisionOne divisionOne;
    private DivisionTwo divisionTwo;
    private String title;
    private String content;
//    private String imgUrl;
    private String address;
    private String coordinate;
    private int point;
}
