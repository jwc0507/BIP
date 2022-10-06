package com.example.week8.dto.response;

import com.example.week8.domain.enums.Board;
import com.example.week8.domain.enums.Category;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class PostResponseDto {
    private Long id;
    private Board board;
    private Category category;
    private String nickname;
    private String profileImgUrl;
    private String content;
    private String address;
    private String coordinate;
    private String imgUrl;
    private int numOfComment;
    private int views;
    private int likes;
    private int point;
    private String timePast;
    private String createdAt;
    private String modifiedAt;
}
