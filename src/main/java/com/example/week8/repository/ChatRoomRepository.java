package com.example.week8.repository;

import com.example.week8.domain.Event;
import com.example.week8.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByEvent(Event event);
    Optional<ChatRoom> findByName(String roomName);
}
