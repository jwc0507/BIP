package com.example.week8.controller;

import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;

    // 채팅방에 있는지 확인
    @RequestMapping (value = "/api/chat/member/{eventId}", method = RequestMethod.GET)
    public ResponseDto<?> getChatMember (@PathVariable Long eventId, HttpServletRequest request) {
        return chatService.getChatMember(eventId, request);
    }

    // 채팅메세지 불러오기
    @RequestMapping (value = "/api/chat/message/{eventId}", method = RequestMethod.GET)
    public ResponseDto<?> getMessageLog (@PathVariable Long eventId, HttpServletRequest request) {
        return chatService.getMessage(eventId, request);
    }

    // 채팅방 나가기
    @RequestMapping (value = "/api/chat/member/{eventId}", method = RequestMethod.DELETE)
    public ResponseDto<?> exitChatRoom (@PathVariable Long eventId, HttpServletRequest request) {
        return chatService.exitChatRoom(eventId, request);
    }
}
