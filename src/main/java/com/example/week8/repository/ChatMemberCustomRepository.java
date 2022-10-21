package com.example.week8.repository;

import com.example.week8.domain.chat.ChatMember;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMemberCustomRepository {
    List<ChatMember> searchUnReadChatMember(boolean status, LocalDateTime lastMessageTime, Long id);
}
