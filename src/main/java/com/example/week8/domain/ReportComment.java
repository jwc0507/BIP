package com.example.week8.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReportComment extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPORT_COMMENT_ID")
    private Long id;

    private Long fromId;  // 신고한 멤버의 아이디
    private Long toId;  // 신고받은 멤버의 아이디
    private Long commentId; // 신고받은 댓글의 아이디

    public ReportComment(Long fromId, Long toId, Long commentId) {
        this.fromId = fromId;
        this.toId = toId;
        this.commentId = commentId;
    }
}