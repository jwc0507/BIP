package com.example.week8.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventListDto {
    private Long id;
    private String title;
    private String eventDateTime;
    private String place;
    private int memberCount;
    private String lastTime;
}