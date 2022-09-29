package com.example.week8.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReceivePointResponseDto {
    private String context;
    private double newCredit;
    private int lastPoint;
}
