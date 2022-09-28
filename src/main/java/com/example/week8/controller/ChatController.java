package com.example.week8.controller;

import com.example.week8.domain.chat.ChatRequestDto;
import com.example.week8.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
// import 생략...

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(ChatRequestDto message, @Header("Authorization") String token) {
        chatService.sendMessage(message, token);
    }
}