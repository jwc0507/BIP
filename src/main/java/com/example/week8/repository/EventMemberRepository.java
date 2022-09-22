package com.example.week8.repository;

import com.example.week8.domain.EventMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventMemberRepository extends JpaRepository<EventMember, Long> {
    Optional<EventMember> findByEventIdAndMemberId(Long eventId, Long memberId);
}
