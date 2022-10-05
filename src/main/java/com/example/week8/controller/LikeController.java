package com.example.week8.controller;

import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    //좋아요 표시
    @PostMapping("api/like/mark/{post_id}")
    public ResponseDto<?> markLike(@PathVariable Long post_id, HttpServletRequest request){
        return likeService.markLike(post_id,request);
    }
    //좋아요 취소
    @PostMapping("api/like/cancel/{post_id}")
    public ResponseDto<?> cancelLike(@PathVariable Long post_id, HttpServletRequest request){
        return  likeService.cancelLike(post_id, request);
    }
    //좋아요 표시한 글 조회
    @GetMapping("api/likelist")
    public ResponseDto<?> getLikeList(HttpServletRequest request){
        return likeService.getLikeList(request);
    }

}
