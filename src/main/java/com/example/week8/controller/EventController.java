package com.example.week8.controller;

import com.example.week8.dto.request.*;
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
     * 약속 목록 조회
     * unit: day, week, month
     */
    @GetMapping("/api/events/list")
    public ResponseDto<?> getAllEvent(@RequestParam("unit") String unit,
                                      @RequestParam("querydate") String inputDate,
                                      HttpServletRequest request) {

        return eventService.getAllEvent(unit, inputDate, request);
    }

    /**
     * 약속 단건 조회
     */
    @GetMapping("/api/events/{eventId}")
    public ResponseDto<?> getEvent(@PathVariable Long eventId , HttpServletRequest request) {
        return eventService.getEvent(eventId, request);
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

    // 방장 확인
    @RequestMapping (value = "/api/event/master/check/{eventId}", method = RequestMethod.GET)
    public ResponseDto<?> setSecondName(@PathVariable Long eventId, HttpServletRequest request) {
        return eventService.chkMaster(eventId, request);
    }

    // 방장 위임
    @RequestMapping (value = "/api/event/master/{eventId}", method = RequestMethod.POST)
    public ResponseDto<?> setSecondName(@PathVariable Long eventId, @RequestBody MasterRequestDto requestDto, HttpServletRequest request) {
        return eventService.changeMaster(eventId, requestDto, request);
    }

    // 약속 맴버 추방
    @RequestMapping (value = "/api/event/master/{eventId}", method = RequestMethod.DELETE)
    public ResponseDto<?> kickMember(@PathVariable Long eventId, @RequestBody MasterRequestDto requestDto, HttpServletRequest request) {
        return eventService.kickMember(eventId, requestDto, request);
    }
}
