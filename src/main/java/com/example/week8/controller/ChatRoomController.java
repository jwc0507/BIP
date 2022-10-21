package com.example.week8.controller;

import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    @RequestMapping (value = "/api/chat/message", method = RequestMethod.GET)
    public ResponseDto<?> getMessageLog (@RequestParam("eventId") Long eventId, @PageableDefault(size = 50) Pageable pageable, HttpServletRequest request) {
        return chatService.getMessage(eventId, pageable, request);
    }

    // 채팅방 나가기
    @RequestMapping (value = "/api/chat/member/{eventId}", method = RequestMethod.DELETE)
    public ResponseDto<?> exitChatRoom (@PathVariable Long eventId, HttpServletRequest request) {
        return chatService.exitChatRoom(eventId, request);
    }
//
//    // 채팅방 접속상태 확인 (입장)
//    @RequestMapping (value = "/api/chat/member/connect/{eventId}", method = RequestMethod.POST)
//    public ResponseDto<?>  enableChatListenStatus (@PathVariable Long eventId, HttpServletRequest request) {
//        return chatService.switchChatListenStatus(eventId, request, true);
//    }

    // 채팅방 접속상태 확인 (나가기)
    @RequestMapping (value = "/api/chat/member/disconnect/{eventId}", method = RequestMethod.POST)
    public ResponseDto<?>  disableChatListenStatus (@PathVariable Long eventId, HttpServletRequest request) {
        return chatService.switchChatListenStatus(eventId, request);
    }
}
