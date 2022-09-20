package com.example.week8.repository;

import com.example.week8.domain.FriendList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendListRepository extends JpaRepository<FriendList, Long> {
}
