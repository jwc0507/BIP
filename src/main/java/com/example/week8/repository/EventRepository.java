package com.example.week8.repository;

import com.example.week8.domain.Event;
import com.example.week8.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface EventRepository extends JpaRepository<Event, Long> {
    void deleteByMaster(Member member);
    List<Event> findAllByMaster(Member member);
}