package com.example.week8.repository;

import com.example.week8.domain.CheckinMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CheckinMemberRepository extends JpaRepository<CheckinMember, Long> {
    Optional<CheckinMember> findByEventId(Long eventId);
    Optional<CheckinMember> findByMemberId(Long memberId);
    List<CheckinMember> findAllByEventId(Long id);

}
