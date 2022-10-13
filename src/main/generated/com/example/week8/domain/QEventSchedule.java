package com.example.week8.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEventSchedule is a Querydsl query type for EventSchedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEventSchedule extends EntityPathBase<EventSchedule> {

    private static final long serialVersionUID = 534528770L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEventSchedule eventSchedule = new QEventSchedule("eventSchedule");

    public final EnumPath<com.example.week8.domain.enums.BeforeTime> beforeTime = createEnum("beforeTime", com.example.week8.domain.enums.BeforeTime.class);

    public final QEvent event;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> targetTime = createDateTime("targetTime", java.time.LocalDateTime.class);

    public QEventSchedule(String variable) {
        this(EventSchedule.class, forVariable(variable), INITS);
    }

    public QEventSchedule(Path<? extends EventSchedule> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEventSchedule(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEventSchedule(PathMetadata metadata, PathInits inits) {
        this(EventSchedule.class, metadata, inits);
    }

    public QEventSchedule(Class<? extends EventSchedule> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.event = inits.isInitialized("event") ? new QEvent(forProperty("event"), inits.get("event")) : null;
    }

}

