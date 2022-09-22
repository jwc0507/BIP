package com.example.week8.domain;

import com.example.week8.dto.EventRequestDto;
import com.example.week8.time.Time;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
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

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventMember> eventMemberList = new ArrayList<>();

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
}
