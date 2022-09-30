package com.example.week8.controller;

import com.example.week8.dto.request.EventRequestDto;
import com.example.week8.dto.request.PostRequestDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.EventService;
import com.example.week8.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
