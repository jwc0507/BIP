package com.example.week8.controller;

import com.example.week8.dto.request.CommentRequestDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성하기
    @RequestMapping(value = "/api/comment/{postId}", method = RequestMethod.POST)
    public ResponseDto<?> createComment (@PathVariable Long postId, @RequestBody @Valid CommentRequestDto requestDto, HttpServletRequest request) {
        return commentService.createComment(postId, requestDto, request);
    }

    // 댓글 목록조회
    @RequestMapping(value = "/api/comment", method = RequestMethod.GET)
    public ResponseDto<?> getCommentList (@RequestParam("postId") Long postId, @PageableDefault(size = 50) Pageable pageable, HttpServletRequest request) {
        return commentService.getCommentList(postId, pageable, request);
    }

    // 댓글 수정하기
    @RequestMapping(value = "/api/comment/update/{commentId}", method = RequestMethod.PUT)
    public ResponseDto<?> updateComment (@PathVariable Long commentId, @RequestBody @Valid  CommentRequestDto requestDto, HttpServletRequest request) {
        return commentService.updateComment(commentId, requestDto, request);
    }

    // 댓글 삭제하기
    @RequestMapping(value = "/api/comment/delete/{commentId}", method = RequestMethod.PUT)
    public ResponseDto<?> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        return commentService.deleteComment(commentId, request);
    }
}
