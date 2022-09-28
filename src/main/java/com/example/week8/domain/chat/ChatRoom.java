package com.example.week8.domain.chat;

import com.example.week8.domain.Event;
import com.example.week8.domain.Timestamped;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatRoom extends Timestamped {

    // 채팅방 번호
    @Id
    private Long id;

    // 채팅방 이름
    @Column (nullable = false)
    private String name;

    // 약속한개에 채팅방 한개
    @JoinColumn(name = "event_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Event event;

    //맴버 객체
}