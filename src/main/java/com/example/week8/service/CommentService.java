package com.example.week8.service;

import com.example.week8.domain.*;
import com.example.week8.domain.enums.CommentStatus;
import com.example.week8.dto.request.CommentRequestDto;
import com.example.week8.dto.response.CommentResponseDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.CommentRepository;
import com.example.week8.repository.PostRepository;
import com.example.week8.repository.ReportCommentRepository;
import com.example.week8.security.TokenProvider;
import com.example.week8.time.Time;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReportCommentRepository reportCommentRepository;
    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;

    /**
     * 댓글 작성
     */
    @Transactional
    public ResponseDto<?> createComment(Long postId, CommentRequestDto requestDto, HttpServletRequest request) {
        // 토큰체크
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        // 멤버객체 불러오기
        Member member = validateMember(request);

        // 게시글 불러오기
        Post post = isPresentPost(postId);
        if (post == null)
            return ResponseDto.fail("해당 게시글이 없습니다.");

        Comment comment = Comment.builder()
                .post(post)
                .member(member)
                .content(requestDto.getContent())
                .status(CommentStatus.normal)
                .build();

        post.addCommentCounter();
        commentRepository.save(comment);

        return ResponseDto.success(CommentResponseDto.builder()
                .id(comment.getId())
                .nickname(comment.getMember().getNickname())
                .content(comment.getContent())
                .createdAt(Time.serializePostDate(comment.getCreatedAt()))
                .modifiedAt(Time.serializePostDate(comment.getModifiedAt()))
                .build());
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public ResponseDto<?> updateComment(Long commentId, CommentRequestDto requestDto, HttpServletRequest request) {
        // 토큰체크
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        // 멤버객체 불러오기
        Member member = validateMember(request);

        // 댓글 불러오기
        Comment comment = isPresentComment(commentId);
        if (comment == null)
            return ResponseDto.fail("해당 댓글이 없습니다.");

        // 권한체크
        if (!validateAuthorityOfComment(comment, member))
            return ResponseDto.fail("수정 권한이 없습니다.");

        comment.updateComment(requestDto.getContent());

        return ResponseDto.success(CommentResponseDto.builder()
                .id(comment.getId())
                .nickname(comment.getMember().getNickname())
                .content(comment.getContent())
                .createdAt(Time.serializePostDate(comment.getCreatedAt()))
                .modifiedAt(Time.serializePostDate(comment.getModifiedAt()))
                .build());
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public ResponseDto<?> deleteComment(Long commentId, HttpServletRequest request) {
        // 토큰체크
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        // 멤버객체 불러오기
        Member member = validateMember(request);

        // 댓글 불러오기
        Comment comment = isPresentComment(commentId);
        if (comment == null)
            return ResponseDto.fail("해당 댓글이 없습니다.");
        if (!comment.getStatus().toString().equals("normal"))
            return ResponseDto.fail("삭제된 댓글입니다.");

        // 게시글 불러오기
        Post post = isPresentPost(comment.getPost().getId());
        if (post == null)
            return ResponseDto.fail("해당 게시글이 없습니다.");

        // 권한체크 (댓글 작성자, 게시글 작성자)
        if (validateAuthorityOfComment(comment, member)) {
            comment.deleteComment(CommentStatus.deleteByOwner);
        }
        else if (validateAuthorityOfPost(post, member)) {
            comment.deleteComment(CommentStatus.deleteByPostOwner);
        }
        else
            return ResponseDto.fail("삭제 권한이 없습니다.");

        post.removeCommentCounter();
        return ResponseDto.success("댓글 삭제 완료");
    }

    /**
     * 댓글 목록보기
     */
    public ResponseDto<?> getCommentList(Long postId, Pageable pageable, HttpServletRequest request) {
        // 토큰체크
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 게시글 불러오기
        Post post = isPresentPost(postId);
        if (post == null)
            return ResponseDto.fail("해당 게시글이 없습니다.");

        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtDesc(post, pageable);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        for(Comment comment : comments) {
            CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                    .id(comment.getId())
                    .nickname(comment.getMember().getNickname())
                    .content(comment.getContent())
                    .createdAt(Time.serializePostDate(comment.getCreatedAt()))
                    .modifiedAt(Time.serializePostDate(comment.getModifiedAt()))
                    .build();
            commentResponseDtoList.add(commentResponseDto);
        }
        return ResponseDto.success(commentResponseDtoList);
    }

    /**
     * 댓글 신고
     */
    public ResponseDto<?> reportComment(Long commentId, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 멤버 조회
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }

        // 댓글 불러오기
        Comment comment = isPresentComment(commentId);
        if (comment == null)
            return ResponseDto.fail("해당 댓글이 없습니다.");
        if (!comment.getStatus().toString().equals("normal"))
            return ResponseDto.fail("삭제된 댓글입니다.");

        // 댓글신고(ReportComment) 객체 생성
        ReportComment report = new ReportComment(member.getId(), comment.getMember().getId(), commentId);
        if (report.getToId().equals(report.getFromId())) {
            return ResponseDto.fail("자신에게 신고할 수 없습니다.");
        }
        if (reportCommentRepository.findByFromIdAndToId(member.getId(), comment.getMember().getId()).isPresent()) {
            return ResponseDto.fail("중복 신고는 불가능합니다.");
        }
        reportCommentRepository.save(report);

        // 신고횟수 적용(댓글)
        int reportCnt = comment.addReportCnt();
        if (reportCnt >= 10) {  // 누적 신고횟수 10 이상일 때 댓글 삭제
            comment.inactivateByReport();
            log.info("신고 10회 누적으로 게시글이 삭제되었습니다.");
        }

        // 신고횟수 적용(작성자)
        Member postWriter = comment.getMember();
        postWriter.addReportCnt();
        if (reportCnt % 10 == 0) {  // 누적 신고횟수 10 누적 시마다 신용도 차감
            postWriter.declineCredit(0.5);
        }

        return ResponseDto.success("신고가 정상적으로 처리되었습니다.");
    }

    //-- 모듈 --//

    /**
     * 게시글 호출
     */
    @Transactional(readOnly = true)
    public Post isPresentPost(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    /**
     * 댓글 호출
     */
    public Comment isPresentComment(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    /**
     * 내가 쓴 댓글들 호출
     */
    public List<Comment> getCommentList(Member member) {
        return commentRepository.findAllByMember(member);
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
     * 게시글 권한 유효성 검사
     */
    private boolean validateAuthorityOfPost(Post post, Member member) {
        return post.getMember().getId().equals(member.getId());
    }

    /**
     * 댓글 작성자 권한 검사
     */
    private boolean validateAuthorityOfComment(Comment comment, Member member) {
        return comment.getMember().getId().equals(member.getId());
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
