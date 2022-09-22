package com.example.week8.controller;

import com.example.week8.dto.EventRequestDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * 약속 생성
     */
    @PostMapping("/api/events")
    public ResponseDto<?> createEvent(@RequestBody EventRequestDto requestDto, HttpServletRequest request) {
        return eventService.createEvent(requestDto, request);
    }
}
