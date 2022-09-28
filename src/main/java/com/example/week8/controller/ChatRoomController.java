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
    @RequestMapping (value = "/api/chat/message/{eventId}", method = RequestMethod.GET)
    public ResponseDto<?> getMessageLog (@PathVariable Long eventId, HttpServletRequest request) {
        return chatService.getMessage(eventId, request);
    }
}
