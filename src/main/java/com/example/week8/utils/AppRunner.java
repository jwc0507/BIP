package com.example.week8.utils;

import com.example.week8.domain.Member;
import com.example.week8.domain.enums.Authority;
import com.example.week8.repository.MemberRepository;
import com.example.week8.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AppRunner implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final WeatherService weatherService;

    @Override
    @Transactional
    public void run(ApplicationArguments args){
        if(memberRepository.findByNickname("탈퇴한 사용자입니다.").isEmpty()) {
            Member member = Member.builder()
                    .nickname("탈퇴한 사용자입니다.")
                    .phoneNumber(UUID.randomUUID().toString())
                    .point(0)
                    .pointOnDay(0L)
                    .credit(0)
                    .firstLogin(true)
                    .password("@")
                    .numOfDone(0)
                    .numOfSelfEvent(0)
                    .userRole(Authority.valueOf("ROLE_TEMP"))
                    .build();
            memberRepository.save(member);
        }
//        weatherService.saveLocalWeatherInfoList();
    }
}
