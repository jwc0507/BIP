package com.example.week8.domain;

import com.example.week8.domain.chat.ChatMember;
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
public class Member extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id; // 멤버id

    @Column(name = "FIRST_LOGIN")
    private boolean firstLogin; //첫 로그인 여부

    @Column(name = "POINT_ON_DAY")
    private Long pointOnDay; //당일 포인트 획득량

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<EventMember> eventMemberList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CheckinMember> checkinMemberList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Post> postList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMember> chatMember;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "owner")
    private List<Friend> friendListOwner = new ArrayList<>();

    @Column(unique = true)
    private Long kakaoId;   // 카카오id

    @Column(unique = true)
    private String naverId;   // 네이버

    @Column(unique = true)
    private String phoneNumber; // 핸드폰 번호

    @Column(unique = true)
    private String email; // 이메일 주소

    @Column(unique = true)
    private String nickname; // 닉네임 (erd 추가)

    @Column(nullable = false)
    private double credit; // 신용점수

    @Column(nullable = false)
    private int point; // 포인트

    @Column(nullable = false)
    private String password;    // 비밀번호 (상수로 일단 넣기)

    @Column
    private String profileImageUrl; // 프로필이미지 url

    @Column
    private int numOfDone; // 약속 이행 수

    @Column
    private int numOfSelfEvent; // 자신과의 약속 이행 수 (erd 추가)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority userRole;     // 유저 권한 (erd에 추가해야함)

    private int reportCnt;

    public void updateNickname(String name) {
        this.nickname = name;
    }

    public void updatePhoneNumber(String number) {
        this.phoneNumber = number;
    }

    public void updateKakaoMember(String email, String url, Long kakaoId) {
        this.kakaoId = kakaoId;
        this.email = email;
        if (profileImageUrl == null)
            this.profileImageUrl = url;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateProfileImageUrl(String url) {
        this.profileImageUrl = url;
    }

    public void updateCreditScore(double score) {
        this.credit = score;
    }

    public void updatePoint(int point) {
        this.point += point;
        if (point > 0)
            pointOnDay += point;
    }

    public void updateSelfEvent() {
        this.numOfSelfEvent++;
    }

    public void updateNumOfDone(int done) {
        numOfDone += done;
    }

    public void chkFirstLogin() {
        if(this.firstLogin) {
            this.point += 100;
            this.firstLogin = false;
        }
    }

    // 신고 횟수 올리기
    public int addReportCnt() {
        this.reportCnt++;
        return reportCnt;
    }

    // 신용도 차감
    public void declineCredit(double credit) {
        this.credit -= credit;
    }
}
