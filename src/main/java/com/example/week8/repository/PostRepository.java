package com.example.week8.repository;

import com.example.week8.domain.Member;
import com.example.week8.domain.Post;
import com.example.week8.domain.enums.Board;
import com.example.week8.domain.enums.Category;
import com.example.week8.domain.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    List<Post> findAllByPostStatusOrderByCreatedAtDesc(PostStatus postStatus);
    List<Post> findAllByBoardAndPostStatusOrderByCreatedAtDesc(Board board, PostStatus postStatus);
    List<Post> findAllByBoardAndCategoryAndPostStatusOrderByCreatedAtDesc(Board board, Category category, PostStatus postStatus);
    List<Post> findAllByMemberAndPostStatus(Member member, PostStatus postStatus);
    Optional<Post> findByIdAndPostStatus(Long postId, PostStatus postStatus);
    List<Post> findAllByMember(Member member);
}
