package com.example.week8.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class DoneOfEvent extends Timestamped{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id; // 약속 id

    @Column (nullable = false)
    private String title; // 약속 이름

    @Column
    private LocalDate eventDate; // 날짜

    @Column (nullable = false)
    private LocalDateTime eventTime; //시간

    @Column
    private String place; // 장소

    @Column
    private String content; //내용

    @Column (nullable = false)
    private int point; // 걸리는 포인트 (약속의 가중치)

}
