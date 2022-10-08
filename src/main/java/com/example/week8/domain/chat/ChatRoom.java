package com.example.week8.domain.chat;

import com.example.week8.domain.Event;
import com.example.week8.domain.Timestamped;
import lombok.*;

import javax.persistence.*;
import java.util.List;

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

    // 챗 멤버 객체
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMember> chatMember;

    // 챗 메세지들
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatMessage> chatMessageList;
}