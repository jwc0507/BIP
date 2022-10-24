package com.example.week8.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWeatherInfo is a Querydsl query type for WeatherInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWeatherInfo extends EntityPathBase<WeatherInfo> {

    private static final long serialVersionUID = -1117175117L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWeatherInfo weatherInfo = new QWeatherInfo("weatherInfo");

    public final QEvent event;

    public final StringPath icon = createString("icon");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath maxTemp = createString("maxTemp");

    public final StringPath minTemp = createString("minTemp");

    public final StringPath name = createString("name");

    public final StringPath probability = createString("probability");

    public final StringPath sky = createString("sky");

    public final StringPath skyDesc = createString("skyDesc");

    public final StringPath temperature = createString("temperature");

    public QWeatherInfo(String variable) {
        this(WeatherInfo.class, forVariable(variable), INITS);
    }

    public QWeatherInfo(Path<? extends WeatherInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWeatherInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWeatherInfo(PathMetadata metadata, PathInits inits) {
        this(WeatherInfo.class, metadata, inits);
    }

    public QWeatherInfo(Class<? extends WeatherInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.event = inits.isInitialized("event") ? new QEvent(forProperty("event"), inits.get("event")) : null;
    }

}

