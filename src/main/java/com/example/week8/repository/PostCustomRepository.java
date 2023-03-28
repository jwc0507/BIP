package com.example.week8.repository;

import com.example.week8.domain.Post;

import java.util.List;

public interface PostCustomRepository {

    List<Post> searchByContent(String content);

    List<Post> searchByNickname(String name);
}
