package com.example.week8.controller;

import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @RequestMapping (value = "/api/weather/{coordinate}", method = RequestMethod.GET)
    public ResponseDto<?> getWeather(@PathVariable String coordinate) {
        return weatherService.getLocalWeather(coordinate);
    }
}
