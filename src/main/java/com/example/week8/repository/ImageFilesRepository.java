package com.example.week8.repository;


import com.example.week8.domain.ImageFile;
import com.example.week8.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageFilesRepository extends JpaRepository<ImageFile, Long> {
    Optional<ImageFile> findByUrl(String url);
    List<ImageFile> findAllByPost(Post post);
}
