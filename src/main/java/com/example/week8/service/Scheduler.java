package com.example.week8.service;

import com.example.week8.domain.Member;
import com.example.week8.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
@EnableAsync
public class Scheduler {  // 스케쥴링할 메소드의 조건 2가지: void의 return을 가짐. 파라미터를 가질 수 없음.
    private final MemberRepository memberRepository;
    private final EventService eventService;

    @Async
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void init() {
        List<Member> memberList = memberRepository.findAll();
        for(Member curMember : memberList){
            curMember.setFirstLogin(true); //첫 로그인 여부 초기화
            curMember.setPointOnDay(0L);   //일일 획득 포인트 초기화
        }
        log.info("로그인 보너스 카운터가 초기화 되었습니다");
    }

    @Async
    @Scheduled(cron = "0 */10 * * * *")
    public void eventAlarm() {
        eventService.eventAlarm();
    }
}