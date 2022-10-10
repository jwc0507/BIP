package com.example.week8.repository;

import com.example.week8.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByFromIdAndToId(Long fromId, Long toId);
}
