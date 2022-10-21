package com.example.week8.repository;

import com.example.week8.domain.chat.ChatMember;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.week8.domain.chat.QChatMember.chatMember;


@Repository
public class ChatMemberCustomRepositoryImpl implements ChatMemberCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public ChatMemberCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<ChatMember> searchUnReadChatMember(boolean status, LocalDateTime lastMessageTime, Long id) {
        return jpaQueryFactory.selectFrom(chatMember).where(chatMember.chatRoom.id.eq(id), chatMember.status.eq(false), compareTime(lastMessageTime)).fetch();
    }


    private BooleanExpression compareTime(LocalDateTime lastMessageTime) {
        if(lastMessageTime == null)
            return null;
        return chatMember.leftTime.before(lastMessageTime);
    }
}
