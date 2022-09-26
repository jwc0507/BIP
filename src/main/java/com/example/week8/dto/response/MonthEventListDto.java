package com.example.week8.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MonthEventListDto {
    private String eventDateTime;
    private int numberOfEventInToday;
}
