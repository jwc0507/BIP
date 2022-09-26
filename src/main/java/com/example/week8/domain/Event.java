package com.example.week8.domain;

import com.example.week8.dto.request.EventRequestDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Event extends Timestamped{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_ID")
    private Long id; // 약속 id

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)  // 약속이 삭제되면 해당 약속과 연관된 EventMember도 고아가 되어 삭제됨
    private List<EventMember> eventMemberList = new ArrayList<>();

    @OneToOne
    private Member master; // 방장 (Event에서는 방장이 누구인지 궁금하지만 member에서는 자기가 방장인지 궁금하지는 않다 = 단방향)

    @Column (nullable = false)
    private String title; // 약속 이름

    @Column
    private LocalDateTime eventDateTime; // 약속 시간

    @Column
    private String place; // 장소

    @Column
    private String content; //내용

    @Column (nullable = false)
    private int point; // 걸리는 포인트 (약속의 가중치)

    /**
     * 약속 수정
     */
    public void updateEvent(EventRequestDto eventRequestDto) {
        this.title = eventRequestDto.getTitle();
        this.eventDateTime = stringToLocalDateTime(eventRequestDto.getEventDateTime());
        this.place = eventRequestDto.getPlace();
        this.content = eventRequestDto.getContent();
        this.point = eventRequestDto.getPoint();
    }

    /**
     * 입력값 형변환 String to LocalDateTime
     */
    public LocalDateTime stringToLocalDateTime(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

        return LocalDateTime.parse(dateStr, formatter);
    }

    // 방장 위임
    public void changeMaster(Member member) {
        this.master = member;
    }
}
