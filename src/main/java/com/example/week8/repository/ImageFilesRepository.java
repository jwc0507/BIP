package com.example.week8.repository;


import com.example.week8.domain.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageFilesRepository extends JpaRepository<ImageFile, Long> {
    Optional<ImageFile> findByUrl(String url);
}
