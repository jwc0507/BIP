package com.example.week8.dto.response;

import com.example.week8.domain.enums.DivisionOne;
import com.example.week8.domain.enums.DivisionTwo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponseDto {
    private Long id;
    private DivisionOne divisionOne;
    private DivisionTwo divisionTwo;
    private String title;
    private String content;
//    private int commentCount;
    private int likes;
}
