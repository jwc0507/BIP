package com.example.week8.dto;

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
    private String content;
    private int point;

}
