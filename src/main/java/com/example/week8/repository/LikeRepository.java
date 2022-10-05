package com.example.week8.repository;

import com.example.week8.domain.Likes;
import com.example.week8.domain.Member;
import com.example.week8.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByMemberAndPost(Member member, Post post);
    void deleteByMemberAndPost(Member member, Post post);
    List<Likes> findAllByMember(Member member);
}
