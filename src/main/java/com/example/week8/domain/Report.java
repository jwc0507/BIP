package com.example.week8.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Report extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPORT_ID")
    private Long id;

    private Long fromId;  // 신고한 멤버의 아이디
    private Long toId;  // 신고받은 멤버의 아이디

    public Report(Long fromId, Long toId) {
        this.fromId = fromId;
        this.toId = toId;
    }
}
