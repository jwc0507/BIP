package com.example.week8.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentAlertDto {
    private String message;
    private String postId;
}
