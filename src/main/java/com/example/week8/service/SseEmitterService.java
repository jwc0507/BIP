package com.example.week8.service;

import com.example.week8.domain.*;
import com.example.week8.domain.enums.AlertType;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.EventMemberRepository;
import com.example.week8.repository.EventRepository;
import com.example.week8.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseEmitterService {
    private static final Map<String, SseEmitter> CLIENTS = new ConcurrentHashMap<>();
    private final TokenProvider tokenProvider;
    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;

    // 구독 테스트
    public SseEmitter subscribeTest(String memberId) {
        String emitterId = makeTimeIncludeId(memberId);
        SseEmitter emitter = new SseEmitter(-1L);

        CLIENTS.put(emitterId, emitter);

        emitter.onTimeout(() -> CLIENTS.remove(emitterId));
        emitter.onCompletion(() -> CLIENTS.remove(emitterId));

        log.info(emitterId+" 테스트 구독완료");
        sendDummyAlert(emitter, emitterId, "테스트");
        return emitter;
    }

    // 보내기 테스트
    public ResponseDto<?> publishTest(HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess()) {
            log.info("토큰오류");
            return ResponseDto.fail("토큰오류");
        }
        // 멤버 조회
        Member member = validateMember(request);

        String num = member.getId().toString();
        Map<String, SseEmitter> map = findAllEmitterStartWithByMemberId(num);

        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
        sseMvcExecutor.execute(() -> map.forEach((id, emitter) -> {
            try {
                emitter.send("알림", MediaType.APPLICATION_JSON);
                log.info(id + " : 발신완료");
                Thread.sleep(100);
            } catch (Exception e) {
                log.warn("disconnected id : {}", id);
            }
        }));
        return ResponseDto.success("발신완료");
    }

    // 알림 구독
    public SseEmitter subscribe(HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess()) {
            log.info("토큰오류");
            return null;
        }
        // 멤버 조회
        Member member = validateMember(request);

        String emitterId = makeTimeIncludeId(member.getId().toString());
        SseEmitter emitter = save(emitterId, new SseEmitter(-1L));

        emitter.onTimeout(() -> CLIENTS.remove(emitterId));
        emitter.onCompletion(() -> CLIENTS.remove(emitterId));
        log.info(emitterId+" : 구독완료");

        sendDummyAlert(emitter, emitterId, member.getNickname());

        return emitter;
    }

    // 더미데이터 / 입장 알림 보내기
    private void sendDummyAlert(SseEmitter emitter, String emitterId, String name) {
        try {
            emitter.send("어서오세요 "+name+"님", MediaType.APPLICATION_JSON);
        }
        catch (IOException e) {
            log.info(e.toString());
            deleteById(emitterId);
        }
    }

    // 약속 알림 보내기
    public void publishInScheduler(Long eventId, AlertType type) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null || type == null)
            return;
        String content = setContext(event.getTitle(), type);
        List<EventMember> eventMemberList = eventMemberRepository.findAllByEventId(eventId);
        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
        sseMvcExecutor.execute(() -> {
            for (EventMember eventMember : eventMemberList) {
                Map<String, SseEmitter> map = findAllEmitterStartWithByMemberId(eventMember.getMember().getId().toString());
                map.forEach((id, emitter) -> {
                    try {
                        emitter.send(content, MediaType.APPLICATION_JSON);
                        log.info(id + ": " + content + ", 발신완료");
                        Thread.sleep(100);
                    } catch (Exception e) {
                        log.warn("disconnected id : {}", id);
                    }
                });
            }
        });
    }

    // 구독 전체 지우기
    public ResponseDto<?> deletePub(HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess()) {
            log.info("토큰오류");
            return ResponseDto.fail("삭제실패");
        }
        // 멤버 조회
        Member member = validateMember(request);
        deleteAllEmitterStartWithId(member.getId().toString());
        return ResponseDto.success("삭제완료");
    }

    // 구독 단건 지우기
    public ResponseDto<?> deleteSinglePub(HttpServletRequest request, String emitterId) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess()) {
            log.info("토큰오류");
            return ResponseDto.fail("삭제실패");
        }
        // 멤버 조회
        deleteById(emitterId);
        return ResponseDto.success("삭제완료");
    }

    /**
     * 모듈
     */

    // 알림 메세지 만들기
    private String setContext(String text, AlertType type) {
        switch (type.toString()) {
            case "DAY":
                return "[" + text + "] 약속이 하루 남았습니다.";
            case "HOUR":
                return "[" + text + "] 약속이 한 시간 남았습니다.";
            case "MIN":
                return "[" + text + "] 약속이 십 분 남았습니다.";
            default:
                return "[" + text + "]";
        }
    }


    // 식별가능한 id생성
    private String makeTimeIncludeId(String memberId) {
        return memberId + "_" + System.currentTimeMillis();
    }

    // Emitter 저장
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        CLIENTS.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    // Emitter 지우기
    public void deleteById(String id) {
        CLIENTS.remove(id);
    }

    // 맴버의 전체 Emitter찾기 (브라우저 탭이 여러개일 경우)
    public Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId) {
        return CLIENTS.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // 회원의 Emitter 지우기 (로그아웃)
    public void deleteAllEmitterStartWithId(String memberId) {
        CLIENTS.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        CLIENTS.remove(key);
                    }
                }
        );
    }

    /**
     * 멤버 유효성 검사
     */
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

    /**
     * 토큰 유효성 검사
     */
    private ResponseDto<?> validateCheck(HttpServletRequest request) {

        // RefreshToken 및 Authorization 유효성 검사
        if (null == request.getHeader("RefreshToken") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }
        Member member = validateMember(request);
        // token 정보 유효성 검사
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }
        return ResponseDto.success(member);
    }

    /**
     * 현재 구독중인 회원의 전체 emitter id를 불러온다.
     */
    public ResponseDto<?> getSubInfo(HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess()) {
            log.info("토큰오류");
            return ResponseDto.fail("토큰 오류");
        }
        // 멤버 조회
        Member member = validateMember(request);
        List<String> emitterList = new ArrayList<>();
        Map<String, SseEmitter> map = findAllEmitterStartWithByMemberId(member.getId().toString());
        map.forEach((id, emitter) -> {
            try {
                emitterList.add(id);
            } catch (Exception e) {
                log.warn("disconnected id : {}", id);
            }
        });
        return ResponseDto.success(emitterList);
    }
}
