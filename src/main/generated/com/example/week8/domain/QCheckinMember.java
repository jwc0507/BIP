package com.example.week8.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCheckinMember is a Querydsl query type for CheckinMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCheckinMember extends EntityPathBase<CheckinMember> {

    private static final long serialVersionUID = 650036440L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCheckinMember checkinMember = new QCheckinMember("checkinMember");

    public final EnumPath<com.example.week8.domain.enums.Attendance> attendance = createEnum("attendance", com.example.week8.domain.enums.Attendance.class);

    public final QEvent event;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public QCheckinMember(String variable) {
        this(CheckinMember.class, forVariable(variable), INITS);
    }

    public QCheckinMember(Path<? extends CheckinMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCheckinMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCheckinMember(PathMetadata metadata, PathInits inits) {
        this(CheckinMember.class, metadata, inits);
    }

    public QCheckinMember(Class<? extends CheckinMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.event = inits.isInitialized("event") ? new QEvent(forProperty("event"), inits.get("event")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

