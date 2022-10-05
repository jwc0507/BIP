package com.example.week8.service;

import com.example.week8.domain.*;
import com.example.week8.domain.enums.EventStatus;
import com.example.week8.dto.TokenDto;
import com.example.week8.dto.request.*;
import com.example.week8.dto.response.*;
import com.example.week8.repository.EventMemberRepository;
import com.example.week8.repository.EventRepository;
import com.example.week8.repository.MemberRepository;
import com.example.week8.repository.RefreshTokenRepository;
import com.example.week8.security.TokenProvider;
import com.example.week8.time.Time;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final EventMemberRepository eventMemberRepository;
    private final EventRepository eventRepository;
    private final FileService fileService;
    private final JavaMailSender javaMailSender;
    private final RefreshTokenRepository refreshTokenRepository;

    private final double MAG_POINT_CREDIT = 0.00025;  // 포인트 환산 신용도 증가 배율 (0.00025가 기본)

    // 닉네임 변경
    @Transactional
    public ResponseDto<?> setNickname(MemberInfoRequestDto requestDto, HttpServletRequest request) {
        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        String newNickname = requestDto.getValue();

        if (memberService.checkNickname(DuplicationRequestDto.builder().value(newNickname).build()).isSuccess()) {
            Member member = (Member) chkResponse.getData();
            Member updateMember = memberRepository.findById(member.getId()).get();

            updateMember.updateNickname(newNickname);
            return ResponseDto.success(UpdateMemberResponseDto.builder()
                    .nickname(updateMember.getNickname())
                    .phoneNumber(updateMember.getPhoneNumber())
                    .email(updateMember.getEmail())
                    .profileImgUrl(updateMember.getProfileImageUrl())
                    .point(updateMember.getPoint())
                    .creditScore(updateMember.getCredit())
                    .numOfDone(updateMember.getNumOfDone())
                    .build());
        }
        return ResponseDto.fail("중복된 닉네임 입니다.");
    }

    // 전화번호 변경
    @Transactional
    public ResponseDto<?> setPhoneNumber(LoginRequestDto requestDto, HttpServletRequest request) {
        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        String newPhoneNumber = requestDto.getPhoneNumber();

        if (!memberService.chkValidCode(newPhoneNumber, requestDto.getAuthCode()))
            return ResponseDto.fail("인증실패 코드를 확인해주세요");

        ResponseDto<?> responseDto = memberService.getMemberByPhoneNumber(DuplicationRequestDto.builder().value(newPhoneNumber).build());

        if (responseDto.isSuccess()) {
            if (responseDto.getData() == null) {
                Member member = (Member) chkResponse.getData();
                Member updateMember = memberRepository.findById(member.getId()).get();

                updateMember.updatePhoneNumber(newPhoneNumber);

                return ResponseDto.success(UpdateMemberResponseDto.builder()
                        .nickname(updateMember.getNickname())
                        .phoneNumber(updateMember.getPhoneNumber())
                        .email(updateMember.getEmail())
                        .profileImgUrl(updateMember.getProfileImageUrl())
                        .point(updateMember.getPoint())
                        .creditScore(updateMember.getCredit())
                        .numOfDone(updateMember.getNumOfDone())
                        .build());
            }
            else {
                return ResponseDto.fail("중복된 전화번호 입니다");
            }
        }
        return ResponseDto.fail(responseDto.getData());
    }

    // 카카오 로그인 전용 전화번호 설정
    @Transactional
    public ResponseDto<?> setKakaoPhoneNumber(LoginRequestDto requestDto, HttpServletRequest request, HttpServletResponse response) {
        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        String newPhoneNumber = requestDto.getPhoneNumber();

        if (!memberService.chkValidCode(newPhoneNumber, requestDto.getAuthCode()))
            return ResponseDto.fail("인증실패 코드를 확인해주세요");

        ResponseDto<?> responseDto = memberService.getMemberByPhoneNumber(DuplicationRequestDto.builder().value(newPhoneNumber).build());

        if (responseDto.isSuccess()) {
            Member member = (Member) chkResponse.getData();
            Member updateMember = memberRepository.findById(member.getId()).get();

            // 없는 전화번호
            if(responseDto.getData() == null) {
                updateMember.updatePhoneNumber(newPhoneNumber);

                return ResponseDto.success(UpdateMemberResponseDto.builder()
                        .nickname(updateMember.getNickname())
                        .phoneNumber(updateMember.getPhoneNumber())
                        .email(updateMember.getEmail())
                        .profileImgUrl(updateMember.getProfileImageUrl())
                        .point(updateMember.getPoint())
                        .creditScore(updateMember.getCredit())
                        .numOfDone(updateMember.getNumOfDone())
                        .build());
            }
            // 만약 먼저 회원가입한 계정에 해당 전화번호가 있다면
            else {
                Member findMember = (Member) responseDto.getData();
                if (findMember.getKakaoId() != null) {
                    return ResponseDto.fail("해당 전화번호에 가입된 카카오 아이디가 있습니다.");
                }
                // 계정 통합
                String email = updateMember.getEmail();
                String url = updateMember.getProfileImageUrl();
                Long kakaoId = updateMember.getKakaoId();

                SecurityContextHolder.clearContext();
                tokenProvider.deleteRefreshToken(updateMember);

                memberRepository.deleteById(updateMember.getId());
                memberRepository.flush();

                findMember.updateKakaoMember(email,url,kakaoId);
                forceLogin(findMember, response);

                findMember.chkFirstLogin();
                return ResponseDto.success(UpdateMemberResponseDto.builder()
                        .nickname(findMember.getNickname())
                        .phoneNumber(findMember.getPhoneNumber())
                        .email(findMember.getEmail())
                        .profileImgUrl(findMember.getProfileImageUrl())
                        .point(findMember.getPoint())
                        .creditScore(findMember.getCredit())
                        .numOfDone(findMember.getNumOfDone())
                        .build());
            }

        }
        return ResponseDto.fail(responseDto.getData());
    }

    private void forceLogin(Member kakaoUser, HttpServletResponse response) {

        TokenDto token = tokenProvider.generateTokenDto(kakaoUser);
        response.addHeader("Authorization", "Bearer " + token.getAccessToken());
        response.addHeader("RefreshToken", token.getRefreshToken());

        if(kakaoUser.isFirstLogin()) {
            kakaoUser.setPoint(kakaoUser.getPoint() + 100);
            kakaoUser.setFirstLogin(false);
        }

        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, token.getAccessToken(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    // 이메일 설정
    @Transactional
    public ResponseDto<?> setEmail(EmailLoginRequestDto requestDto, HttpServletRequest request) {
        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        String newEmail = requestDto.getEmail();

        if (!memberService.chkValidCode(newEmail, requestDto.getAuthCode()))
            return ResponseDto.fail("인증실패 코드를 확인해주세요");

        if (memberService.checkEmail(DuplicationRequestDto.builder().value(newEmail).build()).isSuccess()) {
            Member member = (Member) chkResponse.getData();
            Member updateMember = memberRepository.findById(member.getId()).get();

            updateMember.updateEmail(newEmail);

            return ResponseDto.success(UpdateMemberResponseDto.builder()
                    .nickname(updateMember.getNickname())
                    .phoneNumber(updateMember.getPhoneNumber())
                    .email(updateMember.getEmail())
                    .profileImgUrl(updateMember.getProfileImageUrl())
                    .point(updateMember.getPoint())
                    .creditScore(updateMember.getCredit())
                    .numOfDone(updateMember.getNumOfDone())
                    .build());
        }
        return ResponseDto.fail("중복된 이메일 입니다.");
    }

    @Transactional
    public ResponseDto<?> sendEmailCode(AuthRequestDto requestDto) {
        ResponseDto<?> getAuthCode = memberService.sendAuthCode(requestDto);
        if(!getAuthCode.isSuccess())
            return ResponseDto.fail("코드생성 실패");

        String subject = "[프로미스톡] 이메일 로그인 인증코드입니다";
        String text = "인증번호 ["+getAuthCode.getData()+"] 을 입력해주세요.";

        // simpleMailMessage를 사용하면 텍스트만 보내고 MimeMessage를 사용시 멀티파트로 보냄 (파일전송 가능)
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mailHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            mailHelper.setTo(requestDto.getValue());
            mailHelper.setSubject(subject);
            mailHelper.setText(text);
            javaMailSender.send(mimeMessage);
        }
        catch (MessagingException e) {
            return ResponseDto.fail("잘못된 이메일 주소입니다.");
        }
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setTo(requestDto.getValue());
//        simpleMailMessage.setSubject(subject);
//        simpleMailMessage.setText(text);
//        javaMailSender.send(simpleMailMessage);

        return ResponseDto.success("인증번호 전송완료");
    }

    // RefreshToken 유효성 검사
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

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

    // 유저 정보 보기
    public ResponseDto<?> getMemberInfo(HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;  // 동작할일은 없는 코드

        return ResponseDto.success(UpdateMemberResponseDto.builder()
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
                .profileImgUrl(member.getProfileImageUrl())
                .point(member.getPoint())
                .creditScore(member.getCredit())
                .numOfDone(member.getNumOfDone())
                .build());
    }

    // 프로필 사진 업데이트
    @Transactional
    public ResponseDto<?> setProfileImg(ImgUrlRequestDto requestDto, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;  // 동작할일은 없는 코드

        String getImgUrl = member.getProfileImageUrl();

        // 이미지url재등록
        member.updateProfileImageUrl(requestDto.getImgUrl());

        // s3에서 기존 url 파일을 삭제하는 구문 필요 (이미지 업로드 기능 구현 후 추가함)
        if (getImgUrl != null) {
            fileService.deleteFile(getImgUrl);
        }

        return ResponseDto.success(UpdateMemberResponseDto.builder()
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
                .profileImgUrl(member.getProfileImageUrl())
                .point(member.getPoint())
                .creditScore(member.getCredit())
                .numOfDone(member.getNumOfDone())
                .build());
    }

    // 회원탈퇴
    @Transactional
    public ResponseDto<?> deleteMember(HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;  // 동작할일은 없는 코드

        SecurityContextHolder.clearContext();
        memberRepository.deleteById(member.getId());

        return ResponseDto.success("회원탈퇴 완료");
    }

    // 포인트 소모 (신용도올리기)
    @Transactional
    public ResponseDto<?> conversionPointToCredit(ConversionPointToCreditDto pointToCreditDto, HttpServletRequest request) {
        int point = pointToCreditDto.getPoint();
        String nickname = pointToCreditDto.getNickname();

        // 최소 포인트량 확인
        if (point < 1000)
            return ResponseDto.fail("포인트는 최소 1000부터 사용할 수 있습니다.");

        // 토큰 확인
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;  // 동작할일은 없는 코드

        // 잔여 포인트량 확인
        if (member.getPoint() < point || member.getPoint() < 0)
            return ResponseDto.fail("포인트가 부족합니다.");

        // 자기 자신인지 확인
        Member receiver;
        double magnification;
        if(!member.getNickname().equals(nickname)) {
            // 자신이 아니면 상대 멤버객체를 가져오기
            receiver = memberRepository.findByNickname(nickname).orElse(null);
            if(receiver == null)
                return ResponseDto.fail("받는 사람 닉네임이 올바르지 않습니다.");
            magnification = MAG_POINT_CREDIT*2;   // 포인트로 신용도 올리기 배율을 동일화 하자는 fe요청
        }
        else {
            receiver = member;
            magnification = MAG_POINT_CREDIT*2;
        }

        // 신용도 추가
        if (receiver.getCredit() >= 200)
            return ResponseDto.fail("이미 신용도가 최대치 입니다.");

        double calculationCredit = magnification*point; // 증가할 신용도량
        double newCredit = receiver.getCredit()+calculationCredit;
        double lastCredit = 0;
        // 신용도는 200까지만 증가시킬 수 있음
        if (200 < newCredit) {
            lastCredit = newCredit - 200;
            newCredit = 200;
        }
        // 신용도의 최소치는 0이다.
        else if (0 > newCredit) {
            newCredit = 0;
        }
        receiver.updateCreditScore(newCredit);

        // 남은 포인트 계산
        double lastPoint = lastCredit/magnification;

        // 포인트 감소
        int newPoint = (point*-1)+(int)lastPoint;
        member.updatePoint(newPoint);

        return ResponseDto.success(ReceivePointResponseDto.builder()
                .context(receiver.getNickname()+"님의 신용도 추가가 완료되었습니다.")
                .newCredit(receiver.getCredit())
                .lastPoint(member.getPoint())
                .build());
    }

    // 활동 내역 조회(완료된 약속)
    @Transactional(readOnly = true)
    public ResponseDto<?> getClosedEvent(HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 엔티티 조회
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }

        List<EventMember> eventMemberList = eventMemberRepository.findAllByMemberId(member.getId());
        List<EventListDto> tempList = new ArrayList<>();

        for (EventMember eventMember : eventMemberList) {
            Event event = isPresentEvent(eventMember.getEvent().getId());
            if (event.getEventStatus() == EventStatus.CLOSED)
                tempList.add(convertToDto(event));
        }
        return ResponseDto.success(tempList);
    }

    // 약속 호출
    @Transactional(readOnly = true)
    public Event isPresentEvent(Long id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        return optionalEvent.orElse(null);
    }

    // Event를 EventListDto로 변환
    public EventListDto convertToDto(Event event) {
        return EventListDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .eventDateTime(Time.serializeEventDate(event.getEventDateTime()))
                .place(event.getPlace())
                .memberCount(eventMemberRepository.findAllByEventId(event.getId()).size())
                .lastTime(Time.convertLocaldatetimeToTime(event.getEventDateTime()))
                .build();
    }

}
