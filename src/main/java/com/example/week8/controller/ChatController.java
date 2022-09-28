package com.example.week8.controller;

import com.example.week8.domain.chat.ChatRequestDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;

    // 채팅메세지 보내기
    @MessageMapping("/chat/message")
    public ResponseDto<?> message(ChatRequestDto message, @Header("Authorization") String token) {
        return chatService.sendMessage(message, token);
    }

    // 채팅방 입장
    @MessageMapping("/chat/enter")
    public ResponseDto<?> enterChatRoom(ChatRequestDto message, @Header("Authorization") String token) {
        return chatService.enterChatRoom(message, token);
    }
}