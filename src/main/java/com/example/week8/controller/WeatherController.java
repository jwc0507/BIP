package com.example.week8.controller;

import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @RequestMapping (value = "/api/weather", method = RequestMethod.GET)
    public ResponseDto<?> getWeather(@RequestParam("coordinate") String coordinate) {
        return weatherService.getLocalWeather(coordinate);
    }
}
