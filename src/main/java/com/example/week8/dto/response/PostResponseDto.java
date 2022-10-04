package com.example.week8.dto.response;

import com.example.week8.domain.enums.Board;
import com.example.week8.domain.enums.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponseDto {
    private Long id;
    private Board board;
    private Category category;
    private String nickname;
    private String title;
    private String content;
    private String address;
    private String coordinate;
    private String imgUrl;
    private int numOfComment;
    private int likes;
    private int point;
    private String createdAt;
    private String modifiedAt;
}
