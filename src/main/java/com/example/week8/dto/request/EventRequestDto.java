package com.example.week8.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDto {

    private String title;
    private String eventDateTime;
    private String place;
    private String coordinate;
    private String content;
    private int point;

}
