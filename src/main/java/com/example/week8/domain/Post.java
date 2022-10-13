package com.example.week8.domain;

import com.example.week8.domain.enums.Board;
import com.example.week8.domain.enums.Category;
import com.example.week8.domain.enums.PostStatus;
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

    // lazy타입으로 member가 설정되어 게시글의 작성자를 불러오는 과정에서 닉네임을 알 수 없어서 필드 생성.
//    private String ownerName;

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

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "post")
    private List<Likes> likesList;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "post")
    private List<ImageFile> imageFiles;

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

    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private String coordinate;

    private int reportCnt;  // 신고받은 횟수

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;  // active, inactive

    public Post(Member member, PostRequestDto postRequestDto) {
        this.member = member;
//        this.ownerName = member.getNickname();
        this.board = Board.valueOf(postRequestDto.getBoard());
        this.category = Category.valueOf(postRequestDto.getCategory());
        this.address = postRequestDto.getAddress();
        this.coordinate = postRequestDto.getCoordinate();
        this.content = postRequestDto.getContent();
        this.likes = 0;
        this.views = 0;
        this.point = postRequestDto.getPoint();
        this.address = postRequestDto.getAddress();
        this.coordinate = postRequestDto.getCoordinate();
        this.reportCnt = 0;
        this.numOfComment = 0;
        this.postStatus = PostStatus.active;
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

    // 게시글 비활성화
    public void inactivate() {
        this.postStatus = PostStatus.inactive;
    }
    
    //회원정보 검증
    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }

    // 게시글 수정
    public void updatePost(PostRequestDto postRequestDto) {
        this.board = Board.valueOf(postRequestDto.getBoard());
        this.category = Category.valueOf(postRequestDto.getCategory());
        this.content = postRequestDto.getContent();
        this.address = postRequestDto.getAddress();
        this.coordinate = postRequestDto.getCoordinate();
        this.point = postRequestDto.getPoint();
    }

    public void addLike(){
        this.likes++;
    }

    public void cancelLike(){
        this.likes--;
    }


}
