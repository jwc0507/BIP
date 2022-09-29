package com.example.week8.domain.chat;

import com.example.week8.domain.Member;
import com.example.week8.domain.Timestamped;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
public class ChatMessage extends Timestamped {

    // 메세지 번호
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    // 메세지 내용
    @Column
    private String message;

    // 발신자의 id (멤버한명이 여러개의 메세지를 보냄)
    @JoinColumn(name = "sender_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    // 채팅방 번호 (챗룸 한개에 많은 채팅메세지)
    @JoinColumn(name = "chat_room_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

}