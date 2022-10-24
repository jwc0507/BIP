package com.example.week8.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEvent is a Querydsl query type for Event
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEvent extends EntityPathBase<Event> {

    private static final long serialVersionUID = -1203386549L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEvent event = new QEvent("event");

    public final QTimestamped _super = new QTimestamped(this);

    public final com.example.week8.domain.chat.QChatRoom chatRoom;

    public final ListPath<CheckinMember, QCheckinMember> checkinMemberList = this.<CheckinMember, QCheckinMember>createList("checkinMemberList", CheckinMember.class, QCheckinMember.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final StringPath coordinate = createString("coordinate");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> eventDateTime = createDateTime("eventDateTime", java.time.LocalDateTime.class);

    public final ListPath<EventMember, QEventMember> eventMemberList = this.<EventMember, QEventMember>createList("eventMemberList", EventMember.class, QEventMember.class, PathInits.DIRECT2);

    public final ListPath<EventSchedule, QEventSchedule> eventScheduleList = this.<EventSchedule, QEventSchedule>createList("eventScheduleList", EventSchedule.class, QEventSchedule.class, PathInits.DIRECT2);

    public final EnumPath<com.example.week8.domain.enums.EventStatus> eventStatus = createEnum("eventStatus", com.example.week8.domain.enums.EventStatus.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember master;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath place = createString("place");

    public final NumberPath<Integer> point = createNumber("point", Integer.class);

    public final StringPath title = createString("title");

    public final QWeatherInfo weather;

    public QEvent(String variable) {
        this(Event.class, forVariable(variable), INITS);
    }

    public QEvent(Path<? extends Event> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEvent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEvent(PathMetadata metadata, PathInits inits) {
        this(Event.class, metadata, inits);
    }

    public QEvent(Class<? extends Event> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.chatRoom = inits.isInitialized("chatRoom") ? new com.example.week8.domain.chat.QChatRoom(forProperty("chatRoom"), inits.get("chatRoom")) : null;
        this.master = inits.isInitialized("master") ? new QMember(forProperty("master")) : null;
        this.weather = inits.isInitialized("weather") ? new QWeatherInfo(forProperty("weather"), inits.get("weather")) : null;
    }

}

