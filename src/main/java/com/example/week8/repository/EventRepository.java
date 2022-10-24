package com.example.week8.repository;

import com.example.week8.domain.Event;
import com.example.week8.domain.Member;
import com.example.week8.domain.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    void deleteByMaster(Member member);
    List<Event> findAllByMaster(Member member);
    List<Event> findAllByEventStatusAndEventDateTimeLessThanEqual(EventStatus eventStatus, LocalDateTime nowMinusOneDay);
    List<Event> findAllByEventStatusAndEventDateTimeGreaterThanEqual(EventStatus eventStatus, LocalDateTime localDateTime);
}