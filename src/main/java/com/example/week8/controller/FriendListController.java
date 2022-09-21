package com.example.week8.controller;

import com.example.week8.dto.request.FriendAdditionRequestDto;
import com.example.week8.dto.response.FriendInfoResponseDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class FriendListController {
    private final FriendService friendService;

    //친구 목록 조회
    @RequestMapping(value = "api/friends", method= RequestMethod.GET)
    public ResponseDto<?> getFriendList(HttpServletRequest request) { return friendService.getFriendList(request); }

    // Q. API명세서를 보면, '닉네임으로 친구 추가'와 '전화번호로 친구 추가' API의 requestDto가 다른데,
    // 이럴 경우 requestDto를 각각 하나씩 만들어줘야하는가? 아니면, requestDto를 하나만 만들고, 그 안에 '닉네임'과 '전화번호' 멤버를 정의해야 하는가?

    //닉네임으로 친구 추가
    @RequestMapping(value = "api/frineds/nickname", method= RequestMethod.POST)
    public ResponseDto<?> addFriendByNickname(@RequestBody FriendAdditionRequestDto friendAdditionRequestDto, HttpServletRequest request){
        return friendService.addFriendByNickname(friendAdditionRequestDto,request);
    }

    //전화번호로 친구 추가
    @RequestMapping(value = "api/freinds/phonenumber", method = RequestMethod.POST)
    public ResponseDto<?> addFriendByPhoneNumber(@RequestBody FriendAdditionRequestDto friendAdditionRequestDto, HttpServletRequest request) {
        System.out.println("전화번호로 친구 추가 컨트롤러");
        return friendService.addFriendByPhoneNumber(friendAdditionRequestDto, request);
    }

    //친구 삭제
    @RequestMapping(value = "api/friends/{memberId}")//memberId로 변경
    public ResponseDto<?> deleteFriend(@PathVariable Long memberId, HttpServletRequest request){
        return friendService.deleteFriend(memberId, request);
    }

}
