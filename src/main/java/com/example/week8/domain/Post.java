package com.example.week8.domain;

import com.example.week8.domain.enums.DivisionOne;
import com.example.week8.domain.enums.DivisionTwo;
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

    @Enumerated(EnumType.STRING)
    private DivisionOne divisionOne;

    @Enumerated(EnumType.STRING)
    private DivisionTwo divisionTwo;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "post")
    private List<Comment> comments;

    @Column
    private int likes;

    @Column
    private int numOfComment;

    @Column
    private int totalCountOfComment;

    @Column
    private int views;

    @Column(nullable = false)
    private int point;

    @Column
    private String imgUrl;
    @Column
    private String address;
    @Column
    private String coordinate;

    public Post(Member member, PostRequestDto postRequestDto) {
        this.member = member;
        this.divisionOne = postRequestDto.getDivisionOne();
        this.divisionTwo = postRequestDto.getDivisionTwo();
        this.title = postRequestDto.getTitle();
        this.address = postRequestDto.getAddress();
//        this.imgUrl = postRequestDto.getImgUrl();
        this.coordinate = postRequestDto.getCoordinate();
        this.content = postRequestDto.getContent();
        this.point = postRequestDto.getPoint();
        this.likes = 0;
        this.numOfComment = 0;
    }

    // 조회수 올리기
    public void addViews() {
        this.views += 1;
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

    //회원정보 검증
    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }

    // 게시글 수정
    public void updatePost(PostRequestDto postRequestDto) {
        this.divisionOne = postRequestDto.getDivisionOne();
        this.divisionTwo = postRequestDto.getDivisionTwo();
        this.title = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();

    }

}
