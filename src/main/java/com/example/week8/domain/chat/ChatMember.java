package com.example.week8.domain.chat;

import com.example.week8.domain.Member;
import com.example.week8.domain.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ChatMember extends Timestamped {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "chat_room_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @Column
    private boolean status; // 채팅방에 접속중인지 확인

    @Column
    private LocalDateTime leftTime; // 채팅을 마지막으로 읽은 시간 확인

    @Column
    private LocalDateTime enterTime; // 채팅방에 다시 접속한 시간 확인

    public void setStatus (boolean status) {
        this.status = status;
    }

    public void setLeftTime () {
        leftTime = LocalDateTime.now();
    }

    public void setEnterTime () {
        enterTime = LocalDateTime.now();
    }
}
