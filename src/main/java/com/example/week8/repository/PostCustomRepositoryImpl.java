package com.example.week8.repository;

import com.example.week8.domain.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.week8.domain.QPost.post;

@Repository
public class PostCustomRepositoryImpl implements PostCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public PostCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Post> searchByContent(String content) {
        return jpaQueryFactory.selectFrom(post).where(containContent(content)).fetch();
    }

    @Override
    public List<Post> searchByNickname(String name) {
        return jpaQueryFactory.selectFrom(post).where(eqNickname(name)).fetch();
    }

    private BooleanExpression containContent(String content) {
        if(content == null || content.isEmpty())
            return null;
        return post.content.containsIgnoreCase(content);
    }

    private BooleanExpression eqNickname(String name) {
        if(name == null || name.isEmpty())
            return null;
        return post.member.nickname.eq(name);
    }
}
