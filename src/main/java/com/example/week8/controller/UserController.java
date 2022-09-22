package com.example.week8.controller;

import com.example.week8.dto.request.UpdateMemberRequestDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 닉네임 변경
    @RequestMapping(value = "/api/user/nickname", method = RequestMethod.PUT)
    public ResponseDto<?> updateNickname(@RequestBody UpdateMemberRequestDto requestDto, HttpServletRequest request) {
        return userService.setNickname(requestDto, request);
    }

    // 전화번호 변경
    @RequestMapping(value = "/api/user/phonenumber", method = RequestMethod.PUT)
    public ResponseDto<?> updatePhoneNumber(@RequestBody UpdateMemberRequestDto requestDto, HttpServletRequest request, HttpServletResponse response) {
        return userService.setPhoneNumber(requestDto, request, response);
    }

    // 이메일 설정
    @RequestMapping(value = "/api/user/email", method = RequestMethod.PUT)
    public ResponseDto<?> updateEmail(@RequestBody UpdateMemberRequestDto requestDto, HttpServletRequest request, HttpServletResponse response) {
        return userService.setEmail(requestDto, request);
    }
}
