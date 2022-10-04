package com.example.week8.service;

import com.example.week8.domain.Friend;
import com.example.week8.domain.Member;
import com.example.week8.domain.enums.SearchType;
import com.example.week8.dto.request.FriendAdditionRequestDto;
import com.example.week8.dto.request.FriendSecondNameRequestDto;
import com.example.week8.dto.response.FriendInfoResponseDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.FriendRepository;
import com.example.week8.repository.MemberRepository;
import com.example.week8.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;


@RequiredArgsConstructor
@Service
public class FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    //친구 목록 조회
    @Transactional
    public ResponseDto<?> getFriendList(HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();
        List<Friend> friendList = friendRepository.findAllByOwner(member);
        Collections.sort(friendList);
        List<FriendInfoResponseDto> friendInfoResponseDtoList = new ArrayList<>();

        for (Friend friend : friendList) {
            friendInfoResponseDtoList.add(FriendInfoResponseDto.builder()
                    .id(friend.getFriend().getId())
                    .nicknameByOwner(friend.getSecondName())
                    .nicknameByFriend(friend.getFriend().getNickname())
                    .profileImgUrl(friend.getFriend().getProfileImageUrl())
                    .creditScore(friend.getFriend().getCredit())
                    .build()
            );
        }
        return ResponseDto.success(friendInfoResponseDtoList);
    }

    //닉네임으로 친구 추가
    @Transactional
    public ResponseDto<?> addFriendByNickname(FriendAdditionRequestDto friendAdditionRequestDto, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;  // 동작할일은 없는 코드

        //nickname으로 검색한 member
        Member findedMember = memberRepository.findByNickname(friendAdditionRequestDto.getValue()).orElse(null);
        if (findedMember == null)
            return ResponseDto.fail("닉네임 친구찾기 실패");

        // 본인에 친구걸수 없음
        if(Objects.equals(member.getId(), findedMember.getId()))
            return ResponseDto.fail("본인을 친구등록 할 수 없습니다.");

        // 이미 친구인지 확인
        Optional<?> chkExist = friendRepository.findByOwnerAndFriend(member, findedMember);
        if (chkExist.isPresent())
            return ResponseDto.fail("이미 친구등록된 상대입니다.");


        //Friend type의 객체 생성
        Friend newFriend = Friend.builder()
                .owner(member)
                .friend(findedMember) //사용자가 닉네임 잘 못 입력했을 때, 처리를 해줘야함. isPresnet 사용해서 분기 생성하기.
                .build();

        //새로운 친구 db에 추가.
        friendRepository.save(newFriend);

        return ResponseDto.success(FriendInfoResponseDto.builder()
                .id(newFriend.getFriend().getId())
                .nicknameByOwner(newFriend.getSecondName())
                .nicknameByFriend(newFriend.getFriend().getNickname())
                .profileImgUrl(newFriend.getFriend().getProfileImageUrl())
                .creditScore(newFriend.getFriend().getCredit())
                .build());
    }

    //휴대전화번호로 친구 추가
    @Transactional
    public ResponseDto<?> addFriendByPhoneNumber(FriendAdditionRequestDto friendAdditionRequestDto, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;  // 동작할일은 없는 코드

        //phoneNumber로 검색한 member
        Member findedMember = memberRepository.findByPhoneNumber(friendAdditionRequestDto.getValue()).orElse(null);
        if (findedMember == null)
            return ResponseDto.fail("전화번호 친구찾기 실패");

        // 본인에 친구걸수 없음
        if(Objects.equals(member.getId(), findedMember.getId()))
            return ResponseDto.fail("본인을 친구등록 할 수 없습니다.");

        // 이미 친구인지 확인
        Optional<?> chkExist = friendRepository.findByOwnerAndFriend(member, findedMember);
        if (chkExist.isPresent())
            return ResponseDto.fail("이미 친구등록된 상대입니다.");


        //Friend type으로 새 친구 객체 생성
        Friend newFriend = Friend.builder()
                .owner(member)
                .friend(findedMember)
                .build();

        //새로운 친구 db에 추가.
        friendRepository.save(newFriend);

        return ResponseDto.success(FriendInfoResponseDto.builder()
                .id(newFriend.getFriend().getId())
                .nicknameByOwner(newFriend.getSecondName())
                .nicknameByFriend(newFriend.getFriend().getNickname())
                .profileImgUrl(newFriend.getFriend().getProfileImageUrl())
                .creditScore(newFriend.getFriend().getCredit())
                .build());
    }

    //친구 삭제
    @Transactional
    public ResponseDto<?> deleteFriend(Long friendId, HttpServletRequest request) { //friendId = 친구의 memberId
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();
        Member findedMember = memberRepository.findById(friendId).get();
        Friend friend = isPresentFriend(member, findedMember);
        if (friend == null)
            return ResponseDto.fail("친구삭제 실패");

        friendRepository.delete(friend);
        return ResponseDto.success("친구삭제가 완료되었습니다.");
    }

    private Friend isPresentFriend(Member owner, Member friend) {
        Optional<Friend> findedFriend = friendRepository.findByOwnerAndFriend(owner, friend);
        return findedFriend.orElse(null);
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

    // 친구검색
    public ResponseDto<?> searchFriend(String value, String type, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = (Member) chkResponse.getData();

        Member findedMember;
        // 닉네임으로 검색
        if (type.equals(SearchType.name.toString())) {
            findedMember = memberRepository.findByNickname(value).orElse(null);
            if (findedMember == null)
                return ResponseDto.fail("닉네임을 찾을 수 없습니다.");
        }
        // 전화번호로 검색
        else if (type.equals(SearchType.phone.toString())) {
            findedMember = memberRepository.findByPhoneNumber(value).orElse(null);
            if (findedMember == null)
                return ResponseDto.fail("전화번호를 찾을 수 없습니다.");
        } else {
            return ResponseDto.fail("검색 타입 에러");
        }

        Friend friend = isPresentFriend(member, findedMember);
        if (friend == null)
            return ResponseDto.fail("친구리스트에 없는 멤버입니다.");

        return ResponseDto.success(FriendInfoResponseDto.builder()
                .id(findedMember.getId())
                .nicknameByOwner(friend.getSecondName())
                .nicknameByFriend(findedMember.getNickname())
                .profileImgUrl(findedMember.getProfileImageUrl())
                .creditScore(findedMember.getCredit())
                .build());
    }

    // 유저 검색
    public ResponseDto<?> searchMember(String value, String type, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member getMember = validateMember(request);

        Member findedMember;
        // 닉네임으로 검색
        if (type.equals(SearchType.name.toString())) {
            findedMember = memberRepository.findByNickname(value).orElse(null);
            if (findedMember == null)
                return ResponseDto.fail("닉네임을 찾을 수 없습니다.");
        }
        // 전화번호로 검색
        else if (type.equals(SearchType.phone.toString())) {
            findedMember = memberRepository.findByPhoneNumber(value).orElse(null);
            if (findedMember == null)
                return ResponseDto.fail("전화번호를 찾을 수 없습니다.");
        } else {
            return ResponseDto.fail("검색 타입 에러");
        }

        String secondName = null;
        Friend friend = isPresentFriend(getMember, findedMember);
        if(friend != null)
            secondName = friend.getSecondName();

        return ResponseDto.success(FriendInfoResponseDto.builder()
                .id(findedMember.getId())
                .nicknameByOwner(secondName)
                .nicknameByFriend(findedMember.getNickname())
                .profileImgUrl(findedMember.getProfileImageUrl())
                .creditScore(findedMember.getCredit())
                .build());

    }

    @Transactional
    // 별명넣기
    public ResponseDto<?> setSecondName(FriendSecondNameRequestDto requestDto, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;  // 동작할일은 없는 코드

       Member getFriend = memberRepository.findByNickname(requestDto.getFriendNickname()).orElse(null);
        if (getFriend == null)
            return ResponseDto.fail("닉네임을 찾을 수 없습니다.");

        Friend friend = isPresentFriend(member, getFriend);
        if (friend == null)
            return ResponseDto.fail("친구리스트에 없는 멤버입니다.");

        friend.setSecondName(requestDto.getSecondName());
        return ResponseDto.success(FriendInfoResponseDto.builder()
                .id(friend.getFriend().getId())
                .nicknameByOwner(friend.getSecondName())
                .nicknameByFriend(friend.getFriend().getNickname())
                .profileImgUrl(friend.getFriend().getProfileImageUrl())
                .creditScore(friend.getFriend().getCredit())
                .build());
    }

    //추천 친구 목록 반환 (추천 친구 = 나는 추가하지 않았지만, 나를 추가한 친구)
    @Transactional
    public ResponseDto<?> getRecommandFriendsList(HttpServletRequest request){
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        Member owner = (Member) chkResponse.getData();
        List<Friend> friendsAddedMeList = friendRepository.findAllByFriend(owner); //Friend 테이블에서 오너를 친구로 등록한 친구 모두 찾기.
        List<FriendInfoResponseDto> friendInfoResponseDtoList = new ArrayList<>();

        for(Friend curFriend : friendsAddedMeList) {
            if (friendRepository.findByOwnerAndFriend(owner, curFriend.getOwner()).isEmpty()) {//나는 특정인을 친구로 등록안하고,특정인은 나를 친구로 등록했을 때만 friendInfoResponseDtoList에 추가
                {
                    String nicknameByFriend=null;
                    Friend ownerSideFriend =friendRepository.findByOwnerAndFriend(owner,curFriend.getOwner()).orElse(null);

                    if(ownerSideFriend!=null)
                        nicknameByFriend=ownerSideFriend.getSecondName();

                    friendInfoResponseDtoList.add(FriendInfoResponseDto.builder()
                            .id(curFriend.getOwner().getId())
                            .nicknameByOwner(nicknameByFriend)
                            .nicknameByFriend(curFriend.getOwner().getNickname())
                            .profileImgUrl(curFriend.getOwner().getProfileImageUrl())
                            .creditScore(curFriend.getOwner().getCredit())
                            .build());
                }
            }
        }
        return ResponseDto.success(friendInfoResponseDtoList);
    }
}
