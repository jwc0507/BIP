package com.example.week8.domain;

import com.example.week8.domain.enums.CommentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Comment extends Timestamped{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String content;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "post_id")
    private Post post;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "member_id")
    private Member member;

    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private CommentStatus status;

    public String getContent () {
        if(this.status.toString().equals("deleteByOwner"))
            return "댓글 작성자가 삭제한 댓글입니다.";
        else if(this.status.toString().equals("deleteByPostOwner"))
            return "게시글 작성자가 삭제한 댓글입니다.";
        return this.content;
    }

    public void deleteComment(CommentStatus status) {
        this.status = status;
    }

    public void updateComment(String content) {
        this.content = content;
    }

    public void setTempMember(Member member) {
        this.member = member;
    }

}
