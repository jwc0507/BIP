package com.example.week8.repository;

import com.example.week8.domain.LoginMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface LoginMemberRepository extends JpaRepository<LoginMember, Long> {
    Optional<LoginMember> findByPhoneNumber(String phoneNumber);    // 여러번 인증시도 할 수 있으므로
}
