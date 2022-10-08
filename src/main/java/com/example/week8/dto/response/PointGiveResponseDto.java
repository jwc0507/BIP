package com.example.week8.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PointGiveResponseDto {
    private String message;
    private int lastPoint;
}
