package com.example.week8.domain.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class ChatMessageDto {
    private String sender;
    private String message;
    private String sendTime;
}
