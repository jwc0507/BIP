package com.example.week8.controller;

import com.example.week8.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;


}
