package com.example.week8.repository;

import com.example.week8.domain.Post;
import com.example.week8.domain.enums.DivisionOne;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByModifiedAtDesc();
    List<Post> findAllByDivisionOneOrderByModifiedAtDesc(DivisionOne divisionOne);
}
