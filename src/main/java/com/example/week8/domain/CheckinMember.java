package com.example.week8.domain;

import com.example.week8.domain.enums.Attendance;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CheckinMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHECKIN_MEMBER_ID")
    private Long id; // 체크인멤버 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    @JsonIgnore
    private Event event;

    @OneToOne(fetch = FetchType.LAZY)  // 기본은 즉시로딩
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    private Attendance attendance;

    public CheckinMember(Event event, Member member) {
        this.event = event;
        this.member = member;
        this.attendance = Attendance.NOSHOW;
    }

}
