package com.example.week8.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QReportComment is a Querydsl query type for ReportComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReportComment extends EntityPathBase<ReportComment> {

    private static final long serialVersionUID = -1091452356L;

    public static final QReportComment reportComment = new QReportComment("reportComment");

    public final QTimestamped _super = new QTimestamped(this);

    public final NumberPath<Long> commentId = createNumber("commentId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> fromId = createNumber("fromId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Long> toId = createNumber("toId", Long.class);

    public QReportComment(String variable) {
        super(ReportComment.class, forVariable(variable));
    }

    public QReportComment(Path<? extends ReportComment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReportComment(PathMetadata metadata) {
        super(ReportComment.class, metadata);
    }

}

