package com.example.week8.utils;

import com.example.week8.domain.ImageFile;
import com.example.week8.domain.Member;
import com.example.week8.repository.ImageFilesRepository;
import com.example.week8.repository.MemberRepository;
import com.example.week8.service.EventService;
import com.example.week8.service.FileService;
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
    private final ImageFilesRepository imageFilesRepository;
    private final EventService eventService;
    private final FileService fileService;

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
        eventService.scheduledConfirm();
    }


    @Async
    @Transactional
    @Scheduled(cron="0 0 03 * * ?")
  //  @Scheduled(cron="0 * * * * *")
    public void clearImageData() {
        // s3에서 지우는 작업 (실행시 delete로 s3 프리티어 횟수가 증가하므로 일단은 사용하지 않는게 좋을 것 같음.)
//        List<ImageFile> imageFileList = imageFilesRepository.findAllByPost(null);
//        for(ImageFile imageFile : imageFileList) {
//            fileService.deleteFile(imageFile.getUrl());
//            imageFilesRepository.delete(imageFile);
//        }

        // s3는 비우지않고 테이블만 비우는 작업
        imageFilesRepository.deleteAllByPost(null);

        log.info("이미지 데이터 정리 완료");
    }
}