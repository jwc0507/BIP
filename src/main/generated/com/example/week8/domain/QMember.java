package com.example.week8.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1563282441L;

    public static final QMember member = new QMember("member1");

    public final QTimestamped _super = new QTimestamped(this);

    public final ListPath<CheckinMember, QCheckinMember> checkinMemberList = this.<CheckinMember, QCheckinMember>createList("checkinMemberList", CheckinMember.class, QCheckinMember.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Double> credit = createNumber("credit", Double.class);

    public final StringPath email = createString("email");

    public final ListPath<EventMember, QEventMember> eventMemberList = this.<EventMember, QEventMember>createList("eventMemberList", EventMember.class, QEventMember.class, PathInits.DIRECT2);

    public final BooleanPath firstLogin = createBoolean("firstLogin");

    public final ListPath<Friend, QFriend> friendListOwner = this.<Friend, QFriend>createList("friendListOwner", Friend.class, QFriend.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> kakaoId = createNumber("kakaoId", Long.class);

    public final ListPath<Likes, QLikes> likesList = this.<Likes, QLikes>createList("likesList", Likes.class, QLikes.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath naverId = createString("naverId");

    public final StringPath nickname = createString("nickname");

    public final NumberPath<Integer> numOfDone = createNumber("numOfDone", Integer.class);

    public final NumberPath<Integer> numOfSelfEvent = createNumber("numOfSelfEvent", Integer.class);

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final NumberPath<Integer> point = createNumber("point", Integer.class);

    public final NumberPath<Long> pointOnDay = createNumber("pointOnDay", Long.class);

    public final ListPath<Post, QPost> postList = this.<Post, QPost>createList("postList", Post.class, QPost.class, PathInits.DIRECT2);

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final NumberPath<Integer> reportCnt = createNumber("reportCnt", Integer.class);

    public final EnumPath<com.example.week8.domain.enums.Authority> userRole = createEnum("userRole", com.example.week8.domain.enums.Authority.class);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

