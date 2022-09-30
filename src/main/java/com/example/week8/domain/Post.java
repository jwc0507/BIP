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

    @Column(nullable = false)
    private int likes;

    public Post(Member member, PostRequestDto postRequestDto) {
        this.member = member;
        this.divisionOne = postRequestDto.getDivisionOne();
        this.divisionTwo = postRequestDto.getDivisionTwo();
        this.title = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();
        this.likes = 0;
    }

    // 조회수 갯수 올리기
    public void addLike() {
        this.likes += 1;
    }

}
