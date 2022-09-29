package com.example.week8.repository;

import com.example.week8.domain.chat.ChatMessage;
import com.example.week8.domain.chat.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoomAndCreatedAtGreaterThanEqualOrderByCreatedAtAsc(ChatRoom chatRoom, LocalDateTime createAt, Pageable pageable);
}
