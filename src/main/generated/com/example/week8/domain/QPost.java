package com.example.week8.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = 1762617807L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final QTimestamped _super = new QTimestamped(this);

    public final StringPath address = createString("address");

    public final EnumPath<com.example.week8.domain.enums.Board> board = createEnum("board", com.example.week8.domain.enums.Board.class);

    public final EnumPath<com.example.week8.domain.enums.Category> category = createEnum("category", com.example.week8.domain.enums.Category.class);

    public final ListPath<Comment, QComment> comments = this.<Comment, QComment>createList("comments", Comment.class, QComment.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final StringPath coordinate = createString("coordinate");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<ImageFile, QImageFile> imageFiles = this.<ImageFile, QImageFile>createList("imageFiles", ImageFile.class, QImageFile.class, PathInits.DIRECT2);

    public final NumberPath<Integer> likes = createNumber("likes", Integer.class);

    public final ListPath<Likes, QLikes> likesList = this.<Likes, QLikes>createList("likesList", Likes.class, QLikes.class, PathInits.DIRECT2);

    public final QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Integer> numOfComment = createNumber("numOfComment", Integer.class);

    public final NumberPath<Integer> point = createNumber("point", Integer.class);

    public final EnumPath<com.example.week8.domain.enums.PostStatus> postStatus = createEnum("postStatus", com.example.week8.domain.enums.PostStatus.class);

    public final NumberPath<Integer> reportCnt = createNumber("reportCnt", Integer.class);

    public final NumberPath<Integer> totalCountOfComment = createNumber("totalCountOfComment", Integer.class);

    public final NumberPath<Integer> views = createNumber("views", Integer.class);

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

