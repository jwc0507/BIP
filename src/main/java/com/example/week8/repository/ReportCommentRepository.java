package com.example.week8.repository;

import com.example.week8.domain.ReportComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {
    Optional<ReportComment> findByFromIdAndToIdAndCommentId(Long fromId, Long toId, Long commentId);
}
