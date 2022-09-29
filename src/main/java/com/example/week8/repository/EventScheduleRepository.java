package com.example.week8.repository;

import com.example.week8.domain.EventSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventScheduleRepository extends JpaRepository<EventSchedule, Long> {
    void deleteAllByEventId(Long eventId);
}
