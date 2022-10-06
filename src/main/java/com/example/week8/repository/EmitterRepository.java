package com.example.week8.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {
    // Emitter 저장
    SseEmitter save(String emitterId, SseEmitter sseEmitter);
    // 맴버의 전체 Emitter찾기 (브라우저 탭이 여러개일 경우)
    Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId);
    // Emitter 지우기
    void deleteById(String id);
    // 회원의 Emitter 지우기
    void deleteAllEmitterStartWithId(String memberId);
}