package com.example.week8.controller;

import com.example.week8.dto.request.PostRequestDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 게시글 생성
     */
    @PostMapping("/api/posts")
    public ResponseDto<?> createEvent(@RequestBody PostRequestDto postRequestDto,
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
     * divisionOne: ASK, ANSWER, ALL
     */
    @GetMapping("/api/posts")
    public ResponseDto<?> getAllPost(@RequestParam("division") String divisionOne) {
        return postService.getAllPost(divisionOne);
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/api/posts/{postId}")
    public ResponseDto<?> updatePost(@PathVariable Long postId,
                                     @RequestBody PostRequestDto postRequestDto,
                                     HttpServletRequest request) {
        return postService.updatePost(postId, postRequestDto, request);
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/api/posts/{eventId}")
    public ResponseDto<?> deleteEvent(@PathVariable Long eventId,
                                      HttpServletRequest request) {
        return postService.deletePost(eventId, request);
    }


}
