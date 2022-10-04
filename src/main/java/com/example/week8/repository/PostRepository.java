package com.example.week8.repository;

import com.example.week8.domain.Post;
import com.example.week8.domain.enums.Board;
import com.example.week8.domain.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByModifiedAtDesc();
    List<Post> findAllByBoardOrderByModifiedAtDesc(Board board);
    List<Post> findAllByBoardAndCategoryOrderByModifiedAtDesc(Board board, Category category);
}
