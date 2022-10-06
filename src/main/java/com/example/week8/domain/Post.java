package com.example.week8.domain;

import com.example.week8.domain.enums.Board;
import com.example.week8.domain.enums.Category;
import com.example.week8.dto.request.PostRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    @JoinColumn(name = "MEMBER_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column (nullable = false)
    @Enumerated(EnumType.STRING)
    private Board board;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private String content;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "post")
    private List<Comment> comments;

    @Column(nullable = false)
    private int likes;

    @Column
    private int numOfComment;

    @Column
    private int totalCountOfComment;

    @Column(nullable = false)
    private int views;

    @Column(nullable = false)
    private int point;

    @Column
    private String imgUrl;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private String coordinate;

    private int reportCnt;  // 신고받은 횟수

    public Post(Member member, PostRequestDto postRequestDto) {
        this.member = member;
        this.board = postRequestDto.getBoard();
        this.category = postRequestDto.getCategory();
        this.content = postRequestDto.getContent();
        this.likes = 0;
        this.views = 0;
        this.point = Integer.parseInt(postRequestDto.getPoint());
//        this.imgUrl = postRequestDto.getImgUrl();
        this.address = postRequestDto.getAddress();
        this.coordinate = postRequestDto.getCoordinate();
        this.reportCnt = 0;
        this.numOfComment = 0;
    }

    // 조회수 올리기
    public void addViews() {
        this.views++;
    }

    // 댓글 수 올리기
    public void addCommentCounter() {
        this.totalCountOfComment++;
        this.numOfComment++;
    }

    // 댓글수 내리기
    public void removeCommentCounter() {
        this.numOfComment--;
    }

    // 신고 횟수 올리기
    public int addReportCnt() {
        this.reportCnt++;
        return reportCnt;
    }

    //회원정보 검증
    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }

    // 게시글 수정
    public void updatePost(PostRequestDto postRequestDto) {
        this.board = postRequestDto.getBoard();
        this.category = postRequestDto.getCategory();
        this.content = postRequestDto.getContent();
        this.address = postRequestDto.getAddress();
        this.coordinate = postRequestDto.getCoordinate();
        this.point = Integer.parseInt(postRequestDto.getPoint());
    }

    public void addLike(){
        this.likes++;
    }

    public void cancelLike(){
        this.likes--;
    }


}
