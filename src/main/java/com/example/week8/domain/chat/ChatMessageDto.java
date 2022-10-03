package com.example.week8.domain.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChatMessageDto {
    private String sender;
    private String message;
    private LocalDateTime sendTime;
}
