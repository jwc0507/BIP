package com.example.week8.controller;

import com.example.week8.dto.EventRequestDto;
import com.example.week8.dto.request.InviteMemberDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * 약속 생성
     */
    @PostMapping("/api/events")
    public ResponseDto<?> createEvent(@RequestBody EventRequestDto requestDto,
                                      HttpServletRequest request) {
        return eventService.createEvent(requestDto, request);
    }

    /**
     * 약속 수정
     */
    @PutMapping("/api/events/{eventId}")
    public ResponseDto<?> updateEvent(@PathVariable Long eventId,
                                      @RequestBody EventRequestDto requestDto,
                                      HttpServletRequest request) {
        return eventService.updateEvent(eventId,requestDto, request);
    }

    /**
     * 약속 단건 조회
     */
    @GetMapping("/api/events/{eventId}")
    public ResponseDto<?> getEvent(@PathVariable Long eventId) {
        return eventService.getEvent(eventId);
    }

    /**
     * 약속 삭제
     */
    @DeleteMapping("/api/events/{eventId}")
    public ResponseDto<?> deleteEvent(@PathVariable Long eventId,
                                      HttpServletRequest request) {
        return eventService.deleteEvent(eventId, request);
    }

    /**
     * 약속 초대(약속멤버 추가)
     */
    @PostMapping("/api/events/{eventId}")
    public ResponseDto<?> inviteMember(@PathVariable Long eventId,
                                       @RequestBody InviteMemberDto inviteMemberDto,
                                       HttpServletRequest request) {
        return eventService.inviteMember(eventId, inviteMemberDto, request);
    }

    /**
     * 약속 탈퇴
     */
    @DeleteMapping("api/events/exit/{eventId}")
    public ResponseDto<?> exitEvent(@PathVariable Long eventId,
                                    HttpServletRequest request) {
        return eventService.exitEvent(eventId, request);
    }
}
