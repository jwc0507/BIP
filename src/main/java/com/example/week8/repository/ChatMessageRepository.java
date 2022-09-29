package com.example.week8.repository;

import com.example.week8.domain.chat.ChatMessage;
import com.example.week8.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);

    List<ChatMessage> findAllByChatRoomAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(ChatRoom chatRoom, LocalDateTime createAt);
}
