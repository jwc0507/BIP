package com.example.week8.repository;

import com.example.week8.domain.EventSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventScheduleRepository extends JpaRepository<EventSchedule, Long> {
    Optional<EventSchedule> findByEventId(Long eventId);
}
