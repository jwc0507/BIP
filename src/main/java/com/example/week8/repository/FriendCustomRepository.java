package com.example.week8.repository;

import com.example.week8.domain.Friend;

import java.util.List;

public interface FriendCustomRepository {
    public List<Friend> SearchByNickname(String name, String owner);
    public List<Friend> SearchByPhoneNumber(String number, String owner);
}
