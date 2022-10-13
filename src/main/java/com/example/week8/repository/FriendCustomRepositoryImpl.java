package com.example.week8.repository;

import com.example.week8.domain.Friend;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.week8.domain.QFriend.friend1;

@Repository
public class FriendCustomRepositoryImpl implements FriendCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public FriendCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Friend> SearchByNickname(String name, String owner) {
        return jpaQueryFactory.selectFrom(friend1).where(containNickname(name), friend1.owner.nickname.eq(owner)).fetch();
    }

    @Override
    public List<Friend> SearchByPhoneNumber(String number, String owner) {
        return jpaQueryFactory.selectFrom(friend1).where(containNumber(number), friend1.owner.nickname.eq(owner)).fetch();
    }

    private BooleanExpression containNickname(String name) {
        if(name == null || name.isEmpty())
            return null;
        return friend1.friend.nickname.containsIgnoreCase(name);
    }

    private BooleanExpression containNumber(String number) {
        if(number == null || number.isEmpty())
            return null;
        return friend1.friend.phoneNumber.containsIgnoreCase(number);
    }
}
