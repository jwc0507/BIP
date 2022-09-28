package com.example.week8.domain.chat;

import com.example.week8.domain.enums.MessageType;
import lombok.Getter;
import lombok.Setter;

// 처음으로 입력받는 메세지
@Getter
@Setter
public class ChatRequestDto {
    private MessageType type; // 메시지 타입
    private Long roomId;
    private String message;
}
