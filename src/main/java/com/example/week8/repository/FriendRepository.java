package com.example.week8.repository;

import com.example.week8.domain.Friend;
import com.example.week8.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    Optional<Friend> findByOwnerAndFriend(Member member, Member friend);
}
