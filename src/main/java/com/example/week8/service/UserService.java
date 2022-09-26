package com.example.week8.service;

import com.example.week8.domain.Member;
import com.example.week8.dto.TokenDto;
import com.example.week8.dto.request.*;
import com.example.week8.dto.response.ReceivePointResponseDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.dto.response.UpdateMemberResponseDto;
import com.example.week8.repository.MemberRepository;
import com.example.week8.repository.RefreshTokenRepository;
import com.example.week8.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FileService fileService;

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
    public ResponseDto<?> setPhoneNumber(LoginRequestDto requestDto, HttpServletRequest request, HttpServletResponse response) {
        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        String newPhoneNumber = requestDto.getPhoneNumber();

        if (!memberService.chkValidCode(newPhoneNumber, requestDto.getAuthCode()))
            return ResponseDto.fail("인증실패 코드를 재발급해주세요");


        if (memberService.checkPhoneNumber(DuplicationRequestDto.builder().value(newPhoneNumber).build()).isSuccess()) {
            Member member = (Member) chkResponse.getData();
            Member updateMember = memberRepository.findById(member.getId()).get();

            updateMember.updatePhoneNumber(newPhoneNumber);

            refreshTokenRepository.delete(refreshTokenRepository.findByMember(updateMember).get());

            // 토큰 재생성
            TokenDto tokenDto = tokenProvider.generateTokenDto(updateMember);

            //헤더에 반환 to FE
            response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
            response.addHeader("RefreshToken", tokenDto.getRefreshToken());

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
        return ResponseDto.fail("중복된 전화번호 입니다.");
    }

    // 이메일 설정
    @Transactional
    public ResponseDto<?> setEmail(EmailLoginRequestDto requestDto, HttpServletRequest request) {
        //== token 유효성 검사 ==//
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        String newEmail = requestDto.getEmail();

        if (memberService.chkValidCode(newEmail, requestDto.getAuthCode()))
            return ResponseDto.fail("인증실패 코드를 재발급해주세요");

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

        tokenProvider.deleteRefreshToken(member);

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

        // 자기 자신인지 확인
        Member receiver;
        double magnification;
        if(!member.getNickname().equals(nickname)) {
            // 자신이 아니면 상대 멤버객체를 가져오기
            receiver = memberRepository.findByNickname(nickname).orElse(null);
            if(receiver == null)
                return ResponseDto.fail("받는 사람 닉네임이 올바르지 않습니다.");
            magnification = MAG_POINT_CREDIT*2;
        }
        else {
            receiver = member;
            magnification = MAG_POINT_CREDIT;
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
        receiver.updateCreditScore(newCredit);

        // 남은 포인트 계산
        double lastPoint = lastCredit/magnification;

        // 포인트 감소
        int newPoint = (point*-1)+(int)Math.floor(lastPoint);
        member.updatePoint(newPoint);

        return ResponseDto.success(ReceivePointResponseDto.builder()
                .context(receiver.getNickname()+"님의 신용도 추가가 완료되었습니다.")
                .newCredit(receiver.getCredit())
                .lastPoint(member.getPoint())
                .build());
    }

}
