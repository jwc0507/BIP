package com.example.week8.repository;

import com.example.week8.domain.Comment;
import com.example.week8.domain.Member;
import com.example.week8.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostOrderByCreatedAtDesc(Post post, Pageable pageable);
    List<Comment> findAllByMember(Member member);
}
