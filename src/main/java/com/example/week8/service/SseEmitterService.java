package com.example.week8.service;

import com.example.week8.domain.*;
import com.example.week8.domain.enums.AlertType;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.EmitterRepositoryImpl;
import com.example.week8.repository.EventMemberRepository;
import com.example.week8.repository.EventRepository;
import com.example.week8.repository.MemberRepository;
import com.example.week8.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SseEmitterService {
    private final TokenProvider tokenProvider;
    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final MemberRepository memberRepository;
    private final EmitterRepositoryImpl emitterRepository;

    // 구독 테스트
    public SseEmitter subscribeTest(String memberId) {
        String emitterId = makeTimeIncludeId(memberId);
        SseEmitter emitter = new SseEmitter(60 * 1000L * 15);

        emitterRepository.save(emitterId, emitter);

        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitterRepository.deleteById(emitterId);
        });
        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            emitterRepository.deleteById(emitterId);
        });

        log.info(emitterId+" 테스트 구독완료");
        sendDummyAlert(emitter, emitterId);
        return emitter;
    }

    // 보내기 테스트
    public ResponseDto<?> publishTest(HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess()) {
            log.info("토큰오류 (보내기 테스트)");
            return ResponseDto.fail("토큰오류");
        }
        // 멤버 조회
        Member member = validateMember(request);

        String num = member.getId().toString();
        Map<String, SseEmitter> map = emitterRepository.findAllEmitterStartWithByMemberId(num);

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

    // 보내기 테스트
    public ResponseDto<?> publishTestTwo(Long memberId) {
        // 멤버 조회
        Member member = memberRepository.findById(memberId).orElse(null);

        String num = member.getId().toString();
        Map<String, SseEmitter> map = emitterRepository.findAllEmitterStartWithByMemberId(num);

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

    // 채팅방 초대 알림
    public void pubEventInvite(Long memberId, String eventTitle) {
        Map<String, SseEmitter> map = emitterRepository.findAllEmitterStartWithByMemberId(memberId.toString());

        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
        sseMvcExecutor.execute(() -> map.forEach((id, emitter) -> {
            try {
                emitter.send("["+eventTitle+"] 약속에 초대되셨습니다.", MediaType.APPLICATION_JSON);
                log.info(id + " : 초대 알림 발신완료");
                Thread.sleep(100);
            } catch (Exception e) {
                log.warn("disconnected id : {}", id);
            }
        }));
    }

    // 약속 컨펌 알림
    public void pubEventConfirm(Event event) {
        List<EventMember> eventMemberList = eventMemberRepository.findAllByEventId(event.getId());
        for(EventMember eventMember : eventMemberList) {
            Map<String, SseEmitter> map = emitterRepository.findAllEmitterStartWithByMemberId(eventMember.getMember().getId().toString());

            ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
            sseMvcExecutor.execute(() -> map.forEach((id, emitter) -> {
                try {
                    emitter.send("["+event.getTitle()+"] 약속이 완료되었습니다.", MediaType.APPLICATION_JSON);
                    log.info(id + " : 완료 알림 발신완료");
                    Thread.sleep(100);
                } catch (Exception e) {
                    log.warn("disconnected id : {}", id);
                }
            }));
        }
    }

    // 알림 구독
    public SseEmitter subscribe(HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess()) {
            log.info("act: "+request.getHeader("Authorization"));
            log.info("rft: "+request.getHeader("RefreshToken"));
            log.info("토큰오류 (알림 구독)");
            return null;
        }
        // 멤버 조회
        String id = tokenProvider.getMemberIdByToken(request.getHeader("Authorization"));

        Member member = memberRepository.findById(Long.parseLong(id)).orElse(null);
        if(member == null) {
            log.info("입력받은 id:"  + id);
            return null;
        }

        String emitterId = makeTimeIncludeId(member.getId().toString());
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(60 * 1000L * 15));

        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitterRepository.deleteById(emitterId);
        });
        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            emitterRepository.deleteById(emitterId);
        });

        log.info(emitterId+" : 구독완료");

        sendDummyAlert(emitter, emitterId);

        return emitter;
    }

    // 더미데이터 / 입장 알림 보내기
    private void sendDummyAlert(SseEmitter emitter, String emitterId) {
        try {
            emitter.send("입장", MediaType.APPLICATION_JSON);
        }
        catch (IOException e) {
            log.info(e.toString());
            emitterRepository.deleteById(emitterId);
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
                Map<String, SseEmitter> map = emitterRepository.findAllEmitterStartWithByMemberId(eventMember.getMember().getId().toString());
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
            log.info("토큰오류 (전체 삭제)");
            return ResponseDto.fail("삭제실패");
        }
        // 멤버 조회
        Member member = validateMember(request);
        emitterRepository.deleteAllEmitterStartWithId(member.getId().toString());
        return ResponseDto.success("삭제완료");
    }

    // 구독 단건 지우기
    public ResponseDto<?> deleteSinglePub(HttpServletRequest request, String emitterId) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess()) {
            log.info("토큰오류 (단건 삭제)");
            return ResponseDto.fail("삭제실패");
        }
        // 멤버 조회
        emitterRepository.deleteById(emitterId);
        log.info("이미터 삭제완료");
        return ResponseDto.success("삭제완료");
    }

    /**
     * 현재 구독중인 회원의 전체 emitter id를 불러온다.
     */
    public ResponseDto<?> getSubInfo(HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess()) {
            log.info("토큰오류 (구독 확인)");
            return ResponseDto.fail("토큰 오류");
        }
        // 멤버 조회
        Member member = validateMember(request);
        Map<String, SseEmitter> map = emitterRepository.findAllEmitterStartWithByMemberId(member.getId().toString());
        if(map.isEmpty())
            return ResponseDto.success(false);
        else
            return ResponseDto.success(true);
    }

    /**
     * 현재 구독중인 회원의 전체 emitter id를 불러온다.
     */
    public ResponseDto<?> getSubInfoTwo(Long idx) {
        List<String> emitterIdList = new ArrayList<>();
        Map<String, SseEmitter> map = emitterRepository.findAllEmitterStartWithByMemberId(idx.toString());
        map.forEach((id, emitter) -> {
            try {
                emitterIdList.add(id);
            } catch (Exception e) {
                log.warn("disconnected id : {}", id);
            }
        });
        return ResponseDto.success(emitterIdList);
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
}