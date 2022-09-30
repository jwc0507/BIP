package com.example.week8.service;

import com.example.week8.domain.Member;
import com.example.week8.domain.Post;
import com.example.week8.dto.request.PostRequestDto;
import com.example.week8.dto.response.PostResponseDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.PostRepository;
import com.example.week8.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;

    /**
     * 게시글 작성
     */
    public ResponseDto<?> createPost(PostRequestDto postRequestDto,
                                     HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 멤버 조회
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }

        // 게시글 생성
        Post post = new Post(member, postRequestDto);
        postRepository.save(post);

        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .divisionOne(post.getDivisionOne())
                        .divisionTwo(post.getDivisionTwo())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .likes(post.getLikes())
                        .build()
        );
    }


    /**
     * 멤버 유효성 검사
     */
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

    /**
     * 토큰 유효성 검사
     */
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
}
