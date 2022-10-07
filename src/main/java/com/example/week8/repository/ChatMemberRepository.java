package com.example.week8.repository;

import com.example.week8.domain.Member;
import com.example.week8.domain.chat.ChatMember;
import com.example.week8.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    Optional<ChatMember> findByMemberAndChatRoom(Member member, ChatRoom chatRoom);
    List<ChatMember> findAllByChatRoom(ChatRoom chatRoom);
    List<ChatMember> findAllByMember(Member member);
}
