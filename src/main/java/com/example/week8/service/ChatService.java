package com.example.week8.service;

import com.example.week8.domain.Member;
import com.example.week8.domain.chat.ChatMessage;
import com.example.week8.domain.chat.ChatMessageDto;
import com.example.week8.domain.chat.ChatRequestDto;
import com.example.week8.domain.chat.ChatRoom;
import com.example.week8.domain.enums.MessageType;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.ChatMessageRepository;
import com.example.week8.repository.ChatRoomRepository;
import com.example.week8.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessageSendingOperations messageTemplate;
    private final TokenProvider tokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 메세지 보내기
    @Transactional
    public ResponseDto<?> sendMessage(ChatRequestDto message, String token) {
        // 토큰으로 유저찾기
        Member member = tokenProvider.getMemberByToken(token);
        if (member == null) {
            log.info("토큰오류");
            return ResponseDto.fail("토큰 오류");
        }
        ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId()).orElse(null);
        if (chatRoom == null) {
            log.info("룸 번호 오류");
            return ResponseDto.fail("룸 번호 오류");
        }

        // 메세지 타입확인
        Enum<MessageType> messageTypeEnum = message.getType();
        String messageType = messageTypeEnum.toString();
        ChatMessageDto chatMessageDto;  // 타입별로 넣어줄 메세지내용 dto
        switch (messageType) {
            case "ENTER":
                chatMessageDto = ChatMessageDto.builder()
                        .sender("알림")
                        .message(member.getNickname() + "님이 입장하셨습니다.")
                        .build();
                break;
            case "TALK":
                chatMessageDto = ChatMessageDto.builder()
                        .sender(member.getNickname())
                        .message(message.getMessage())
                        .build();
                break;
            default:  // 개발용 (오류 검증을 위한 부분, 배포단계에서는 삭제필요)
                chatMessageDto = ChatMessageDto.builder()
                        .sender("알림")
                        .message("메세지 타입이 잘못되었습니다.")
                        .build();
                break;
        }
        // 메세지 보내기
        messageTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), chatMessageDto);

        // 보낸 메세지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(member)
                .message(message.getMessage())
                .build();

        chatMessageRepository.save(chatMessage);
        return ResponseDto.success("메세지 보내기 성공");
    }

}
