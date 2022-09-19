package com.example.week8.controller;

import com.example.week8.dto.request.AuthRequestDto;
import com.example.week8.dto.request.DuplicationRequestDto;
import com.example.week8.dto.request.LoginRequestDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 로그인 or 회원가입
    // 회원가입 후 자동 로그인을 시키고 로그인 결과로 닉네임과 이메일을 반환하게 추가 구현해야할듯. 만약 널값이라면 프론트에서 자동으로 닉네임 변경창으로 보내주면 좋을 것 같음.
    @RequestMapping (value = "/api/member/login", method = RequestMethod.POST)
    public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        return memberService.createMember(requestDto, response);
    }

    // 로그아웃
    @RequestMapping (value = "/api/member/logout", method = RequestMethod.POST)
    public ResponseDto<?> logout(HttpServletRequest request) {
        return memberService.logout(request);
    }

    // 문자 인증코드생성
    @RequestMapping (value = "/api/member/auth/sms", method = RequestMethod.POST)
    public ResponseDto<?> sendSMSCode(@RequestBody AuthRequestDto requestDto) {
        return memberService.sendSMSCode(requestDto);
    }

    // 이메일 인증코드생성 (기 생성된 멤버에 한해서)
    @RequestMapping (value = "/api/member/auth/email", method = RequestMethod.POST)
    public ResponseDto<?> sendEmailCode(@RequestBody AuthRequestDto requestDto) {
        return memberService.sendEmailCode(requestDto);
    }

    // 임시 인증코드 생성 (테스트용)
    @RequestMapping (value = "/api/member/auth/test", method = RequestMethod.POST)
    public ResponseDto<?> sendAuthCode(@RequestBody AuthRequestDto requestDto) {
        return memberService.sendAuthCode(requestDto);
    }

    // 전화번호 중복 확인
    @RequestMapping(value = "/api/member/chkphonenumber", method = RequestMethod.POST)
    public ResponseDto<?> checkDuplicationPhoneNumber(@RequestBody DuplicationRequestDto requestDto) {
        return memberService.checkPhoneNumber(requestDto);
    }

    // 닉네임 중복 확인
    @RequestMapping(value = "/api/member/chknickname", method = RequestMethod.POST)
    public ResponseDto<?> checkDuplicationNickname(@RequestBody DuplicationRequestDto requestDto) {
        return memberService.checkNickname(requestDto);
    }

    // 닉네임 중복 확인
    @RequestMapping(value = "/api/member/chkemail", method = RequestMethod.POST)
    public ResponseDto<?> checkDuplicationemail(@RequestBody DuplicationRequestDto requestDto) {
        return memberService.checkEmail(requestDto);
    }
}
