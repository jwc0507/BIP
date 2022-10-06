package com.example.week8.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponseDto {
    private Long id;
    private String nickname;
    private String content;
    private String createdAt;
    private String modifiedAt;
}
