package com.example.week8.domain;

import com.example.week8.domain.enums.BeforeTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class EventSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    @JsonIgnore
    private Event event;

    @Enumerated(EnumType.STRING)
    private BeforeTime BeforeTime;

    @Column
    private LocalDateTime targetTime;

    public EventSchedule(Event event) {
        this.event = event;
    }

//    public EventScheduleDay(Event event) {
//        this.event = event;
//        this.before = Before.DAY;
//        this.targetTime = event.getEventDateTime().minusDays(1);
//    }
//
//    public EventScheduleHour(Event event) {
//        this.event = event;
//        this.before = Before.HOUR;
//        this.targetTime = event.getEventDateTime().minusHours(1);
//    }
//
//    public EventScheduleMinute(Event event) {
//        this.event = event;
//        this.before = Before.MINUTE;
//        this.targetTime = event.getEventDateTime().minusMinutes(10);
//    }
//

//    private LocalDateTime beforeWeek;
//    private LocalDateTime beforeDay;
//    private LocalDateTime beforeHour;
//    private LocalDateTime beforeMinute;

//    public EventSchedule(Event event) {
//        this.event = event;
//        this.beforeWeek = event.getEventDateTime().minusWeeks(1);
//        this.beforeDay = event.getEventDateTime().minusDays(1);
//        this.beforeHour = event.getEventDateTime().minusHours(1);
//        this.beforeMinute = event.getEventDateTime().minusMinutes(10);
//    }

}