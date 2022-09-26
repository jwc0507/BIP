package com.example.week8.domain;

import com.example.week8.domain.enums.Authority;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class Member extends Timestamped{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id; // 멤버id


    @Column (unique = true)
    private Long kakaoId;   // 카카오id

    @Column (unique = true)
    private String naverId;   // 네이버

    @Column (unique = true)
    private String phoneNumber; // 핸드폰 번호

    @Column (unique = true)
    private String email; // 이메일 주소

    @Column (unique = true)
    private String nickname; // 닉네임 (erd 추가)

    @Column (nullable = false)
    private double credit; // 신용점수

    @Column (nullable = false)
    private int point; // 포인트

    @Column (nullable = false)
    private String password;    // 비밀번호 (상수로 일단 넣기)

    @Column
    private String profileImageUrl; // 프로필이미지 url (erd에 추가해야함)

    @Column
    private int numOfDone; // 약속 이행 수 (erd 추가)

    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private Authority userRole;     // 유저 권한 (erd에 추가해야함)

    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "owner")
    private List<Friend> friendList = new ArrayList<>();

    public void updateNickname(String name) {
        this.nickname = name;
    }

    public void updatePhoneNumber(String number) {
        this.phoneNumber = number;
    }

    public void updateEmail(String email) {
        this.email = email;
    }
    public void updateProfileImageUrl (String url) {
        this.profileImageUrl = url;
    }
}
