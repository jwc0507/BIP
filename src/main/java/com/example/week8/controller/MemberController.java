package com.example.week8.controller;

import com.example.week8.dto.request.AuthRequestDto;
import com.example.week8.dto.request.DuplicationRequestDto;
import com.example.week8.dto.request.EmailLoginRequestDto;
import com.example.week8.dto.request.LoginRequestDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.KakaoOauthService;
import com.example.week8.service.MemberService;
import com.example.week8.service.NaverOauthService;
import com.example.week8.service.SmsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final SmsService smsService;
    private final KakaoOauthService kakaoOauthService;
    private final NaverOauthService naverOauthService;

    // 로그인
    @RequestMapping (value = "/api/member/login", method = RequestMethod.POST)
    public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        return memberService.createMember(requestDto, response);
    }

    // 이메일 로그인
    @RequestMapping (value = "/api/member/login/email", method = RequestMethod.POST)
    public ResponseDto<?> emailLogin(@RequestBody EmailLoginRequestDto requestDto, HttpServletResponse response) {
        return memberService.emailLogin(requestDto, response);
    }

    // 카카오 로그인
    @RequestMapping (value = "/api/member/kakaologin", method = RequestMethod.GET)
    public ResponseDto<?> kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoOauthService.kakaoLogin(code, response);
    }

    // 네이버 로그인
    @RequestMapping (value = "/api/member/naverlogin", method = RequestMethod.GET)
    public ResponseDto<?> naverlogin(@RequestParam("code") String code,@RequestParam("state") String state, HttpServletResponse response) throws JsonProcessingException {
        return naverOauthService.naverlogin(code, state, response);
    }

    // 회원가입
    @RequestMapping (value = "/api/member/signup", method = RequestMethod.POST)
    public ResponseDto<?> signup(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        return memberService.createMember(requestDto, response);
    }

    // 로그아웃
    @RequestMapping (value = "/api/member/logout", method = RequestMethod.POST)
    public ResponseDto<?> logout(HttpServletRequest request) {
        return memberService.logout(request);
    }

    // 문자 인증코드생성
    @RequestMapping (value = "/api/member/auth/sms", method = RequestMethod.POST)
    public ResponseDto<?> sendSMSCode(@RequestBody AuthRequestDto requestDto) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        return smsService.sendSms(requestDto);
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
