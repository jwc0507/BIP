package com.example.week8.domain;

import com.example.week8.domain.enums.DivisionOne;
import com.example.week8.domain.enums.DivisionTwo;
import com.example.week8.dto.request.PostRequestDto;
import lombok.*;

import javax.persistence.*;

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

//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Comment> comments;

    @Column
    private int likes;

    @Column
    private int views;

    @Column(nullable = false)
    private int point;

    private String imgUrl;
    private String address;
    private String coordinates;

    public Post(Member member, PostRequestDto postRequestDto) {
        this.member = member;
        this.divisionOne = postRequestDto.getDivisionOne();
        this.divisionTwo = postRequestDto.getDivisionTwo();
        this.title = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();
        this.likes = 0;
    }

    // 조회수 올리기
    public void addViews() {
        this.views += 1;
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
