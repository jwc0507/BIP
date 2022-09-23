package com.example.week8.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class EventMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    //==생성 메서드==//
    public static EventMember createEventMember(Member member, Event event) {
        EventMember eventMember = new EventMember();
        eventMember.setMember(member);
        eventMember.setEvent(event);

        return eventMember;
    }


}
