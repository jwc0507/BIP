package com.example.week8.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseAllDto {
    private Long id;
    private String nickname;
    private String board;
    private String category;
    private String content;
    private int numOfComment;
    private String firstImgUrl;
    private int views;
    private int likes;
    private int point;
    private String timePast;
    private String createdAt;
    private String modifiedAt;
}

