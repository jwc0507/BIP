package com.example.week8.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String code;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.error = errorCode.getStatus().name();
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }
    @Getter
    @AllArgsConstructor
    public enum ErrorCode {

        // 400 BAD_REQUEST
        BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
        // 404 NOT_FOUND
        POSTS_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글 정보를 찾을 수 없습니다."),
        // 405 METHOD_NOT_ALLOWED
        METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),
        // 500 INTERNAL_SERVER_ERROR
        INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류입니다."),
        COORDINATE_EMPTY_ERROR(HttpStatus.BAD_REQUEST, "좌표값 비어있음 에러입니다."),
        DATETIME_PARSE_ERROR(HttpStatus.BAD_REQUEST, "날짜 값 에러입니다."),
        ;

        private final HttpStatus status;
        private final String message;
    }
}
