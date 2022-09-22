package com.example.week8.service;

import com.example.week8.domain.Event;
import com.example.week8.domain.EventMember;
import com.example.week8.domain.Member;
import com.example.week8.dto.EventRequestDto;
import com.example.week8.dto.response.EventResponseDto;
import com.example.week8.dto.response.MemberResponseDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.EventMemberRepository;
import com.example.week8.repository.EventRepository;
import com.example.week8.security.TokenProvider;
import com.example.week8.time.Time;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final TokenProvider tokenProvider;

    /**
     * 약속 생성
     */
    public ResponseDto<?> createEvent(@RequestBody EventRequestDto requestDto,
                                      HttpServletRequest request) {

        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND");
        }

        // 엔티티 조회
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }

        // 약속 생성
        Event event = Event.builder()
                .title(requestDto.getTitle())
                .eventDateTime(stringToLocalDateTime(requestDto.getEventDateTime()))
                .place(requestDto.getPlace())
                .content(requestDto.getContent())
                .point(requestDto.getPoint())
                .build();
        eventRepository.save(event);

        // 약속 멤버 생성
        EventMember eventMember = EventMember.createEventMember(member, event);  // 생성 시에는 약속을 생성한 member만 존재
        eventMemberRepository.save(eventMember);


        // MemberResponseDto에 Member 담기
        List<MemberResponseDto> list = new ArrayList<>();
        MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .id(member.getId())
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
                .credit(member.getCredit())
                .point(member.getPoint())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
        list.add(memberResponseDto);

        return ResponseDto.success(
                EventResponseDto.builder()
                        .id(event.getId())
                        .memberList(list)
                        .title(event.getTitle())
                        .eventDateTime(event.getEventDateTime())
                        .place(event.getPlace())
                        .createdAt(event.getCreatedAt())
                        .lastTime(Time.convertLocaldatetimeToTime(event.getEventDateTime()))
                        .content(event.getContent())
                        .point(event.getPoint())
                        .build()
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
     * 입력값 형변환 String to LocalDateTime
     */
    public LocalDateTime stringToLocalDateTime(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

        return LocalDateTime.parse(dateStr, formatter);
    }

}
