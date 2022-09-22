package com.example.week8.repository;

import com.example.week8.domain.EventMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventMemberRepository extends JpaRepository<EventMember, Long> {
}
