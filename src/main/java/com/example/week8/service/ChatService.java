package com.example.week8.service;

import com.example.week8.domain.EventMember;
import com.example.week8.domain.Member;
import com.example.week8.domain.chat.*;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.*;
import com.example.week8.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessageSendingOperations messageTemplate;
    private final TokenProvider tokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final EventMemberRepository eventMemberRepository;
    // 이미 채팅방에 있는 멤버인지 확인
    @Transactional (readOnly = true)
    public ResponseDto<?> getChatMember(Long eventId, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;  // 동작할일은 없는 코드

        ChatRoom chatRoom = chatRoomRepository.findById(eventId).orElse(null);

        Optional<ChatMember> chatMember = chatMemberRepository.findByMemberAndChatRoom(member, chatRoom);
        if (chatMember.isPresent())
            return ResponseDto.fail("이미 존재하는 회원입니다.");
        return ResponseDto.success("채팅방에 없는 회원입니다.");
    }

    // 채팅방 입장
    @Transactional
    public ResponseDto<?> enterChatRoom(ChatRequestDto message, String token) {
        // 토큰으로 유저찾기
        Long id = Long.parseLong(tokenProvider.getMemberIdByToken(token));
        Member member = memberRepository.findById(id).orElse(null);
        if (member == null) {
            log.info("토큰오류");
            return ResponseDto.fail("토큰 오류");
        }
        ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId()).orElse(null);
        if (chatRoom == null) {
            log.info("룸 번호 오류");
            return ResponseDto.fail("룸 번호 오류");
        }
        // 이벤트 참여자가 아닐 시 fail
        Long eventId = message.getRoomId();
        EventMember eventMember = eventMemberRepository.findByEventIdAndMemberId(eventId, member.getId()).orElse(null);
        if(eventMember == null)
            return ResponseDto.fail("약속 멤버가 아닙니다.");

        // 이미 채팅방에 있는 멤버면 막아야함.
        if (chatMemberRepository.findByMemberAndChatRoom(member, chatRoom).isPresent())
            return ResponseDto.fail("이미 존재하는 회원입니다.");
        // 없다면 채팅방 멤버목록에 넣기
        chatMemberRepository.save(ChatMember.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build());

        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .sender("알림")
                .message(member.getNickname() + "님이 입장하셨습니다.")
                .sendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일 - a hh:mm ").withLocale(Locale.forLanguageTag("ko"))))
                .build();

        // 메세지 보내기
        messageTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), chatMessageDto);

        return ResponseDto.success("입장 성공");
    }

    // 채팅방 나가기
    @Transactional
    public ResponseDto<?> exitChatRoom(Long eventId, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;  // 동작할일은 없는 코드

        ChatRoom chatRoom = chatRoomRepository.findById(eventId).orElse(null);
        Optional<ChatMember> chatMember = chatMemberRepository.findByMemberAndChatRoom(member, chatRoom);
        if (chatMember.isEmpty())
            return ResponseDto.fail("채팅방에 없는 회원입니다.");

        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .sender("알림")
                .message(member.getNickname() + "님이 채팅방에서 나가셨습니다.")
                .sendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일 - a hh:mm ").withLocale(Locale.forLanguageTag("ko"))))
                .build();

        // 메세지 보내기
        messageTemplate.convertAndSend("/sub/chat/room/" + eventId, chatMessageDto);

        chatMember.get().setStatus(false);

        chatMemberRepository.deleteById(chatMember.get().getId());

        return ResponseDto.success("나가기 완료");
    }

    // 전체 채팅방에서 나가기
    @Transactional
    public void exitAllChatRoom(Member member) {
        List<ChatMember> chatMembers = chatMemberRepository.findAllByMember(member);
        for (ChatMember chatMember : chatMembers) {
            ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                    .sender("알림")
                    .message(member.getNickname() + "님이 채팅방에서 나가셨습니다.")
                    .sendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일 - a hh:mm ").withLocale(Locale.forLanguageTag("ko"))))
                    .build();

            // 메세지 보내기
            messageTemplate.convertAndSend("/sub/chat/room/" + chatMember.getChatRoom().getId(), chatMessageDto);

            chatMemberRepository.deleteById(chatMember.getId());
        }
    }

    // 메세지 보내기
    @Transactional
    public ResponseDto<?> sendMessage(ChatRequestDto message, String token) {
        // 토큰으로 유저찾기
        Long id = Long.parseLong(tokenProvider.getMemberIdByToken(token));
        Member member = memberRepository.findById(id).orElse(null);
        if (member == null) {
            log.info("토큰오류");
            return ResponseDto.fail("토큰 오류");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId()).orElse(null);
        if (chatRoom == null) {
            log.info("룸 번호 오류");
            return ResponseDto.fail("룸 번호 오류");
        }

        ChatMember chatMember = chatMemberRepository.findByMemberAndChatRoom(member,chatRoom).orElse(null);
        if(chatMember == null)
            return ResponseDto.fail("채팅 멤버를 찾을 수 없습니다.");

        String dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일 - a hh:mm ").withLocale(Locale.forLanguageTag("ko")));

        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .sender(member.getNickname())
                .message(message.getMessage())
                .sendTime(dateNow)
                .build();

        // 메세지 보내기
        messageTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), chatMessageDto);

        // 보낸 메세지 저장 (db바뀔때 timestamp 없애고 위의 값을 저장하는것으로 바꾸기)

        if(message.getMessage() != null) {
            ChatMessage chatMessage = ChatMessage.builder()
                    .chatRoom(chatRoom)
                    .senderId(member.getId())
                    .sendTime(dateNow)
                    .message(message.getMessage())
                    .build();
            chatMessageRepository.save(chatMessage);
        }
        chatRoom.setLastMessageTime(LocalDateTime.now());

        return ResponseDto.success("메세지 보내기 성공");
    }


    // 기존 채팅방 메세지들 불러오기
    @Transactional
    public ResponseDto<?> getMessage(Long roomId, Pageable pageable, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;

        Optional<ChatRoom> getChatRoom = chatRoomRepository.findById(roomId);
        ChatRoom chatRoom;
        if (getChatRoom.isPresent())
            chatRoom = getChatRoom.get();
        else
            return ResponseDto.fail("채팅방을 찾을 수 없습니다.");

        ChatMember chatMember = chatMemberRepository.findByMemberAndChatRoom(member,chatRoom).orElse(null);
        if(chatMember == null)
            return ResponseDto.fail("채팅 멤버를 찾을 수 없습니다.");

        //      LocalDateTime localDateTime = LocalDateTime.of(2022, 9, 28, 18, 0, 0);

        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoomAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(chatRoom, chatMember.getCreatedAt(), pageable);
        List<ChatMessageDto> chatMessageDtos = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessageList) {

            Member getMember = memberRepository.findById(chatMessage.getSenderId()).orElse(null);
            if(getMember == null)
                getMember = memberRepository.findById(1L).orElse(null);

            assert getMember != null;
            ChatMessageDto chatMsgResponseDto = ChatMessageDto.builder()
                    .sender(getMember.getNickname())
                    .message(chatMessage.getMessage())
                    .sendTime(chatMessage.getSendTime())
                    .build();
            chatMessageDtos.add(chatMsgResponseDto);
        }
        chatMember.setEnterTime();
        chatMember.setStatus(true);

        return ResponseDto.success(chatMessageDtos);
    }

    // 채팅방 읽기모드 상태변경
    @Transactional
    public ResponseDto<?> switchChatListenStatus(Long roomId, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;

        Optional<ChatRoom> getChatRoom = chatRoomRepository.findById(roomId);
        ChatRoom chatRoom;
        if (getChatRoom.isPresent())
            chatRoom = getChatRoom.get();
        else
            return ResponseDto.fail("채팅방을 찾을 수 없습니다.");

        ChatMember chatMember = chatMemberRepository.findByMemberAndChatRoom(member,chatRoom).orElse(null);
        if(chatMember == null)
            return ResponseDto.fail("채팅 멤버를 찾을 수 없습니다.");

        chatMember.setLeftTime();
        chatMember.setStatus(false);
        return ResponseDto.success("접속 해제 완료");
    }


    private ResponseDto<?> validateCheck(HttpServletRequest request) {
        if (null == request.getHeader("RefreshToken") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }
        return ResponseDto.success(member);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}