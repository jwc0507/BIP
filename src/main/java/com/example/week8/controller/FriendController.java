package com.example.week8.controller;

import com.example.week8.dto.request.FriendAdditionRequestDto;
import com.example.week8.dto.request.FriendSecondNameRequestDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class FriendController {
    private final FriendService friendService;

    //친구 목록 조회
    @RequestMapping(value = "api/friends", method= RequestMethod.GET)
    public ResponseDto<?> getFriendList(HttpServletRequest request) { return friendService.getFriendList(request); }


    //닉네임으로 친구 추가
    @RequestMapping(value = "api/friends/nickname", method= RequestMethod.POST)
    public ResponseDto<?> addFriendByNickname(@RequestBody FriendAdditionRequestDto friendAdditionRequestDto, HttpServletRequest request){
        return friendService.addFriendByNickname(friendAdditionRequestDto,request);
    }

    //전화번호로 친구 추가
    @RequestMapping(value = "api/friends/phonenumber", method = RequestMethod.POST)
    public ResponseDto<?> addFriendByPhoneNumber(@RequestBody FriendAdditionRequestDto friendAdditionRequestDto, HttpServletRequest request) {
        System.out.println("전화번호로 친구 추가 컨트롤러");
        return friendService.addFriendByPhoneNumber(friendAdditionRequestDto, request);
    }

    //친구 삭제
    @RequestMapping(value = "api/friends/{memberId}")//memberId=삭제하고자 하는 친구의 memeberId
    public ResponseDto<?> deleteFriend(@PathVariable Long memberId, HttpServletRequest request){
        return friendService.deleteFriend(memberId, request);
    }

    // 친구검색
    @RequestMapping (value = "/api/friends/search", method = RequestMethod.GET)
    public ResponseDto<?> searchFriend(@RequestParam("q") String value, @RequestParam("type") String type, HttpServletRequest request) {
        return friendService.searchFriend(value, type, request);
    }

    // 유저 검색
    @RequestMapping (value = "/api/search", method = RequestMethod.GET)
    public ResponseDto<?> searchMember(@RequestParam("q") String value, @RequestParam("type") String type, HttpServletRequest request) {
        return friendService.searchMember(value, type, request);
    }

    // 친구 별명 추가
    @RequestMapping (value = "/api/friends/secondname", method = RequestMethod.PUT)
    public ResponseDto<?> setSecondName(@RequestBody FriendSecondNameRequestDto requestDto, HttpServletRequest request) {
        return friendService.setSecondName(requestDto, request);
    }
    //추천 친구 목록 반환 (추천 친구 = 나는 추가하지 않았지만, 나를 추가한 친구)
    @RequestMapping (value = "/api/friends/recommandlist", method = RequestMethod.GET)
    public ResponseDto<?> getRecommandFriendsList(HttpServletRequest request) {
        return friendService.getRecommandFriendsList(request);
    }
}
