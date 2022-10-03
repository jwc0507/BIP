package com.example.week8.service;

import com.example.week8.domain.Member;
import com.example.week8.domain.Post;
import com.example.week8.domain.enums.DivisionOne;
import com.example.week8.dto.request.PostRequestDto;
import com.example.week8.dto.response.PostResponseAllDto;
import com.example.week8.dto.response.PostResponseDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.PostRepository;
import com.example.week8.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                        .nickname(post.getMember().getNickname())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .likes(post.getLikes())
                        .point(post.getPoint())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );
    }

    /**
     * 게시글 수정
     */
    public ResponseDto<?> updatePost(Long postId,
                                     PostRequestDto postRequestDto,
                                     HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 멤버 조회
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }

        // 게시글 조회
        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("존재하지 않는 게시글 id 입니다.");
        }

        // 권한 유효성 검사
        if (!validateAuthority(post, member)) {
            return ResponseDto.fail("작성자만 수정할 수 있습니다.");
        }

        // 게시글 수정
        post.updatePost(postRequestDto);

        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .divisionOne(post.getDivisionOne())
                        .divisionTwo(post.getDivisionTwo())
                        .nickname(post.getMember().getNickname())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .likes(post.getLikes())
                        .point(post.getPoint())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );
    }

    /**
     * 게시글 단건 조회
     */
    @Transactional(readOnly = true)
    public ResponseDto<?> getPost(Long postId) {

        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("존재하지 않는 게시글 id 입니다.");
        }

        // 조회수 추가
        post.addViews();

        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .divisionOne(post.getDivisionOne())
                        .divisionTwo(post.getDivisionTwo())
                        .nickname(post.getMember().getNickname())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .likes(post.getLikes())
                        .point(post.getPoint())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );
    }

    /**
     * 게시글 전체 조회
     */
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost(String divisionOne) {

        if (divisionOne.equals("ASK")) {
            List<Post> postList = postRepository.findAllByDivisionOneOrderByModifiedAtDesc(DivisionOne.ASK);
            List<PostResponseAllDto> postResponseAllDtoList = new ArrayList<>();
            for (Post post : postList) {
                postResponseAllDtoList.add(
                        PostResponseAllDto.builder()
                                .id(post.getId())
                                .nickname(post.getMember().getNickname())
                                .title(post.getTitle())
                                .content(post.getContent())
                                .likes(post.getLikes())
                                .point(post.getPoint())
                                .createdAt(post.getCreatedAt())
                                .modifiedAt(post.getModifiedAt())
                                .build()
                );
            }
            return ResponseDto.success(postResponseAllDtoList);
        } else if (divisionOne.equals("ANSWER")) {
            List<Post> postList = postRepository.findAllByDivisionOneOrderByModifiedAtDesc(DivisionOne.ANSWER);
            List<PostResponseAllDto> postResponseAllDtoList = new ArrayList<>();
            for (Post post : postList) {
                postResponseAllDtoList.add(
                        PostResponseAllDto.builder()
                                .id(post.getId())
                                .nickname(post.getMember().getNickname())
                                .title(post.getTitle())
                                .content(post.getContent())
                                .likes(post.getLikes())
                                .point(post.getPoint())
                                .createdAt(post.getCreatedAt())
                                .modifiedAt(post.getModifiedAt())
                                .build()
                );
            }
            return ResponseDto.success(postResponseAllDtoList);
        } else {
            List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
            List<PostResponseAllDto> postResponseAllDtoList = new ArrayList<>();
            for (Post post : postList) {
                postResponseAllDtoList.add(
                        PostResponseAllDto.builder()
                                .id(post.getId())
                                .nickname(post.getMember().getNickname())
                                .title(post.getTitle())
                                .content(post.getContent())
                                .likes(post.getLikes())
                                .point(post.getPoint())
                                .createdAt(post.getCreatedAt())
                                .modifiedAt(post.getModifiedAt())
                                .build()
                );
            }
            return ResponseDto.success(postResponseAllDtoList);
        }
    }



    /**
     * 게시글 삭제
     */
    public ResponseDto<?> deletePost(Long postId,
                                     HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 멤버 조회
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }

        // 게시글 조회
        Post post = isPresentPost(postId);

        // 권한 유효성 검사
        if (!validateAuthority(post, member)) {
            return ResponseDto.fail("작성자만 삭제할 수 있습니다.");
        }

        // 게시글 삭제
        postRepository.deleteById(postId);

        return ResponseDto.success("게시글이 삭제되었습니다.");
    }
    

    //-- 모듈 --//

    /**
     * 게시글 호출
     */
    @Transactional(readOnly = true)
    public Post isPresentPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElse(null);
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
     * 권한 유효성 검사
     */
    private boolean validateAuthority(Post post, Member member) {
        return post.getMember().getId().equals(member.getId());
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
