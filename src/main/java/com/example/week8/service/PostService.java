package com.example.week8.service;

import com.example.week8.domain.Member;
import com.example.week8.domain.Post;
import com.example.week8.domain.enums.Board;
import com.example.week8.domain.enums.Category;
import com.example.week8.dto.request.PostRequestDto;
import com.example.week8.dto.response.PostResponseAllDto;
import com.example.week8.dto.response.PostResponseDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.PostRepository;
import com.example.week8.security.TokenProvider;
import com.example.week8.time.Time;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

        return getResponseDto(post);
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
        return getResponseDto(post);
    }

    /**
     * 게시글 단건 조회
     * api 호출 시 데이터 변동사항(조회수 증가)이 발생하기 때문에 readOnly 옵션 미사용
     */
    public ResponseDto<?> getPost(Long postId) {

        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("존재하지 않는 게시글 id 입니다.");
        }

        // 조회수 추가
        post.addViews();

        return getResponseDto(post);
    }

    /**
     * 게시글 전체 조회
     */
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost(String divisionOne) {

        List<Post> postList;
        List<PostResponseAllDto> postResponseAllDtoList = new ArrayList<>();

        Board boardType = getBoard(divisionOne);
        if (boardType == null)
            return ResponseDto.fail("게시판 종류를 확인해주세요");

        if (divisionOne.equals("request")) {
            postList = postRepository.findAllByBoardOrderByModifiedAtDesc(Board.request);
        }
        else if (divisionOne.equals("donation")) {
            postList = postRepository.findAllByBoardOrderByModifiedAtDesc(Board.donation);
        }
        else {
            postList = postRepository.findAllByOrderByModifiedAtDesc();
        }
        return getResponseDto(postList, postResponseAllDtoList);
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


    /**
     * 상세 카테고리 검색
     */
    public ResponseDto<?> getCategoryList(String board, String category) {

        List<Post> postList;
        List<PostResponseAllDto> postResponseAllDtoList = new ArrayList<>();
        Board boardType = getBoard(board);
        if (boardType == null)
            return ResponseDto.fail("게시판 종류를 확인해주세요");
        Category categoryType = getCategory(category);
        if (categoryType == null)
            return ResponseDto.fail("상세카테고리를 확인해주세요");

        postList = postRepository.findAllByBoardAndCategoryOrderByModifiedAtDesc(boardType, categoryType);

        return getResponseDto(postList, postResponseAllDtoList);
    }


    //-- 모듈 --//

    // ENUM MAPPING (BOARD TYPE)
    public static final Map<String, Board> boardMap = new HashMap<>();

    static {
        for (Board type : Board.values()) {
            boardMap.put(type.toString(), type);
        }
    }

    public static Board getBoard(String type) {
        return boardMap.get(type);
    }

    // ENUM MAPPING (CATEGORY TYPE)
    public static final Map<String, Category> categoryMap = new HashMap<>();

    static {
        for (Category type : Category.values()) {
            categoryMap.put(type.toString(), type);
        }
    }

    public static Category getCategory(String type) {
        return categoryMap.get(type);
    }

    private ResponseDto<?> getResponseDto(Post post) {
        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .board(post.getBoard())
                        .category(post.getCategory())
                        .nickname(post.getMember().getNickname())
                        .profileImgUrl(post.getMember().getProfileImageUrl())
                        .content(post.getContent())
                        .address(post.getAddress())
                        .coordinate(post.getCoordinate())
                        .numOfComment(post.getNumOfComment())
                        .views(post.getViews())
                        .likes(post.getLikes())
                        .point(post.getPoint())
                        .timePast(Time.convertLocaldatetimeToTimePast(post.getCreatedAt()))
                        .createdAt(Time.serializePostDate(post.getCreatedAt()))
                        .modifiedAt(Time.serializePostDate(post.getModifiedAt()))
                        .build()
        );
    }

    private ResponseDto<?> getResponseDto(List<Post> postList, List<PostResponseAllDto> postResponseAllDtoList) {
        for (Post post : postList) {
            postResponseAllDtoList.add(
                    PostResponseAllDto.builder()
                            .id(post.getId())
                            .nickname(post.getMember().getNickname())   // 에러있음
                            .board(post.getBoard().toString())
                            .category(post.getCategory().toString())
                            .content(post.getContent())
                            .views(post.getViews())
                            .likes(post.getLikes())
                            .point(post.getPoint())
                            .numOfComment(post.getNumOfComment())
                            .timePast(Time.convertLocaldatetimeToTimePast(post.getCreatedAt()))
                            .createdAt(Time.serializePostDate(post.getCreatedAt()))
                            .modifiedAt(Time.serializePostDate(post.getModifiedAt()))
                            .build()
            );
        }
        return ResponseDto.success(postResponseAllDtoList);
    }

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
