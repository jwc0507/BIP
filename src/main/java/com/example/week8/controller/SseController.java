package com.example.week8.controller;

import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SseController {
    private final SseEmitterService sseEmitterService;

    // SSE subTest (실제 배포단계에선 주석 또는 삭제)
    @RequestMapping(value = "/api/subscribe/test", method = RequestMethod.GET)
    public SseEmitter subscribeTest(String id) {
        return sseEmitterService.subscribeTest(id);
    }

    // SSE Sub
    @GetMapping("/api/member/subscribe")
    public SseEmitter subscribe(String memberId) {
//        return sseEmitterService.subscribe(memberId);
        return null;
    }

    // SSE pubTest (실제 배포단계에선 주석 또는 삭제)
     @GetMapping("/api/publish")
    public ResponseDto<?> publish(HttpServletRequest request) {
//       return sseEmitterService.publishTest(request);
       return null;
    }

    // Emitter 지우기
    @GetMapping("/api/sse/delete")
    public ResponseDto<?> deleteEmitter(HttpServletRequest request) {
//        return sseEmitterService.deletePub(request);
        return null;
    }

    @GetMapping("/api/sse/getSubInfo")
    public ResponseDto<?> getSubInfo(HttpServletRequest request) {
//        return sseEmitterService.getSubInfo(request);
        return null;
    }

    @GetMapping("/api/sse/getSubInfotwo/{id}")
    public ResponseDto<?> getSubInfotwo(@PathVariable Long id) {
//        return sseEmitterService.getSubInfotwo(id);
        return null;
    }
}