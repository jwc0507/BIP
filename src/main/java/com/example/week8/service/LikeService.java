package com.example.week8.service;

import com.example.week8.domain.Likes;
import com.example.week8.domain.Member;
import com.example.week8.domain.Post;
import com.example.week8.domain.enums.PostStatus;
import com.example.week8.dto.response.LikeResponseDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.LikeRepository;
import com.example.week8.repository.PostRepository;
import com.example.week8.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;

    //좋아요 표시
    @Transactional
    public ResponseDto<?> markLike(Long post_id, HttpServletRequest request){
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();

        Post post = postRepository.findById(post_id).orElse(null);

        if(post ==null)
            return ResponseDto.fail("게시글 없음 or 게시글 ID 오류");
        if(likeRepository.findByMemberAndPost(member,post).orElse(null)!=null)
            return ResponseDto.fail("이미 해당 게시물을 '좋아하는' 상태입니다.");

        Likes like = Likes.builder()
                    .member(member)
                    .post(post)
                    .build();

        likeRepository.save(like);
        post.addLike();
        return ResponseDto.success(LikeResponseDto.builder()
                .post_id(like.getPost().getId())
                .category(like.getPost().getCategory())
                .build());
    }
    //좋아요 취소
    @Transactional
    public ResponseDto<?> cancelLike(Long post_id, HttpServletRequest request){
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();
        Post post = postRepository.findById(post_id).orElse(null);
        if(post ==null)
            return ResponseDto.fail("게시글 없음 or 게시글 ID 오류");

        Likes like= likeRepository.findByMemberAndPost(member,post).orElse(null);
        if(like ==null)
            return ResponseDto.fail("이미 해당 게시물을 '좋아하지 않는' 상태입니다.");
        likeRepository.deleteByMemberAndPost(member,post);
        post.cancelLike();
        return ResponseDto.success(LikeResponseDto.builder()
                .post_id(like.getPost().getId())
                .category(like.getPost().getCategory())
                .build());
    }

    //현재 좋아요가 눌렸는지 확인
    public ResponseDto<?> chkLikePost(Long id, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        Member member = validateMember(request);
        Post post = postRepository.findById(id).orElse(null);
        if(post ==null)
            return ResponseDto.fail("게시글 없음 or 게시글 ID 오류");
        Likes like= likeRepository.findByMemberAndPost(member,post).orElse(null);
        if(like ==null)
            return ResponseDto.success(false);
        return ResponseDto.success(true);
    }

    //좋아하는 게시글 목록 반환
    @Transactional
    public ResponseDto<?> getLikeList(HttpServletRequest request){
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        Member member = (Member) chkResponse.getData();

        List<Likes> likeList = likeRepository.findAllByMember(member);
        List<LikeResponseDto> likeResponseDtos = new ArrayList<>();

        for(Likes like : likeList)
        {
            Post post = like.getPost();
            if(post.getPostStatus().equals(PostStatus.active)) {
                likeResponseDtos.add(LikeResponseDto.builder()
                        .post_id(like.getPost().getId())
                        .category(like.getPost().getCategory())
                        .build());
            }
        }
        return ResponseDto.success(likeResponseDtos);
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
    @javax.transaction.Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }


}
