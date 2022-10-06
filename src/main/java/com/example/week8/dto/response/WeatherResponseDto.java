package com.example.week8.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WeatherResponseDto {
    private String name;
    private String temperature;
    private String maxTemp;
    private String minTemp;
    private String probability;
    private String sky;
    private String skyDesc;
    private String icon;
}
