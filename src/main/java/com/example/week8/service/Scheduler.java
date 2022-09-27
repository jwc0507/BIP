package com.example.week8.service;

import com.example.week8.domain.Member;
import com.example.week8.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class Scheduler {
    private final MemberRepository memberRepository;
    @Scheduled(cron = "59 59 23 * * *")
    public void init() {
        List<Member> memberList = memberRepository.findAll();
        for(Member curMember : memberList){
            curMember.setFirstLogin(true); //첫 로그인 여부 초기화
            curMember.setPointOnDay(0L);   //일일 획득 포인트 초기화
        }
    }
}
