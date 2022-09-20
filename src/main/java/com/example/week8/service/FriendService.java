package com.example.week8.service;
import com.example.week8.domain.FriendList;
import com.example.week8.domain.Member;
import com.example.week8.dto.response.FriendInfoResponseDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.FriendListRepository;
import com.example.week8.repository.MemberRepository;
import com.example.week8.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FriendService {

    private final FriendListRepository friendListRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    //친구 목록 조회
    @Transactional
    ResponseDto<List<FriendInfoResponseDto>> getFriendList(HttpServletRequest request){

        ResponseDto<List<FriendInfoResponseDto>> chkResponse = validateCheck(request);
        if(!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();

        FriendList friendList = friendListRepository.find
        List<FriendInfoResponseDto> friendInfoResponseDtoList = new ArrayList<>();
        for()
    }

    // 토큰체크
    private ResponseDto<?> validateCheck(HttpServletRequest request) {
        if (null == request.getHeader("RefreshToken") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }
        return ResponseDto.success(member);
    }

    // refreshtoken으로 유저찾기
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }


}
