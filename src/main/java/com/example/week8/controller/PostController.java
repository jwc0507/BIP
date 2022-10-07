package com.example.week8.controller;

import com.example.week8.domain.enums.Board;
import com.example.week8.dto.request.PostPointGiveRequestDto;
import com.example.week8.dto.request.PostRequestDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 게시글 생성
     */
    @PostMapping("/api/posts")
    public ResponseDto<?> createEvent(@RequestBody @Valid PostRequestDto postRequestDto,
                                      HttpServletRequest request) {
        return postService.createPost(postRequestDto, request);
    }

    /**
     * 게시글 단건 조회
     */
    @GetMapping("/api/posts/{postId}")
    public ResponseDto<?> getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    /**
     * 게시글 전체 조회
     * divisionOne: request, donation, all
     */
    @GetMapping("/api/posts")
    public ResponseDto<?> getAllPost(@RequestParam("board") String board) {
        return postService.getAllPost(board);
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/api/posts/{postId}")
    public ResponseDto<?> updatePost(@PathVariable Long postId,
                                     @RequestBody @Valid PostRequestDto postRequestDto,
                                     HttpServletRequest request) {
        return postService.updatePost(postId, postRequestDto, request);
    }

    /**
     * 게시글 삭제
     */
    @PutMapping("/api/posts/{eventId}")
    public ResponseDto<?> deleteEvent(@PathVariable Long eventId,
                                      HttpServletRequest request) {
        return postService.deletePost(eventId, request);
    }

    /**
     * 기부요청 게시판 카테고리 조회
     */
    @GetMapping("/api/posts/request")
    public ResponseDto<?> getRequestList(@RequestParam("category") String category) {
        return postService.getCategoryList(Board.request.toString(), category);
    }

    /**
     * 기부하기 게시판 카테고리 조회
     */
    @GetMapping("/api/posts/donation")
    public ResponseDto<?> getDonationList(@RequestParam("category") String category) {
        return postService.getCategoryList(Board.donation.toString(), category);
    }
    
    /**
     * 게시글 신고
     */
    @PostMapping("/api/posts/report/{postId}")
    public ResponseDto<?> report(@PathVariable Long postId,
                                 HttpServletRequest request) {
        return postService.report(postId, request);
    }

    //포인트증여
    @RequestMapping(value = "/api/posts/point/give/{postId}", method = RequestMethod.PUT)
    public ResponseDto<?> givePoint(@PathVariable Long postId, @RequestBody @Valid PostPointGiveRequestDto requestDto, HttpServletRequest request) {
        return postService.givePoint(postId, requestDto, request);
    }


}
