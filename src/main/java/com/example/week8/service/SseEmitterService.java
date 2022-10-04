package com.example.week8.service;

import com.example.week8.repository.EmitterRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private final EmitterRepositoryImpl emitterRepository;

    // 식별가능한 id생성
    private String makeTimeIncludeId(Long memberId) {
        return memberId + "_" + System.currentTimeMillis();
    }

    public void subscribe(Long memberId) {
        String emitterId = makeTimeIncludeId(memberId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(1L));
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
//        String eventId = makeTimeIncludeId(memberId);
//        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + memberId + "]");
    }
}
