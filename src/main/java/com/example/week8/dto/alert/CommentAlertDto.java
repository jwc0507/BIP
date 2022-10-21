package com.example.week8.dto.alert;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentAlertDto {
    private String message;
    private String postId;
}
