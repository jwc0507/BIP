package com.example.week8.dto.response;

import com.example.week8.domain.enums.DivisionOne;
import com.example.week8.domain.enums.DivisionTwo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponseDto {
    private Long id;
    private DivisionOne divisionOne;
    private DivisionTwo divisionTwo;
    private String nickname;
    private String title;
    private String content;
//    private List<Comment> commentList;
//    private int commentCount;
    private int likes;
    private int point;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
