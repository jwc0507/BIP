package com.example.week8.ExceptionHandler;


import com.example.week8.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    // 405에러
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException: {}", e.getMessage());
        return ResponseEntity
                .status(ErrorResponse.ErrorCode.METHOD_NOT_ALLOWED.getStatus().value())
                .body(new ErrorResponse(ErrorResponse.ErrorCode.METHOD_NOT_ALLOWED));
    }

//    // 500에러 (이거있으면 모든 exception을 처리해서 로그로 오류찾기힘듬)
//    @ExceptionHandler(Exception.class)
//    protected ResponseEntity<ErrorResponse> handleException(final Exception e) {
//        log.error("handleException: {}", e.getMessage());
//        return ResponseEntity
//                .status(ErrorResponse.ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value())
//                .body(new ErrorResponse(ErrorResponse.ErrorCode.INTERNAL_SERVER_ERROR));
//    }

    // 날짜 타입 에러
    @ExceptionHandler(DateTimeParseException.class)
    protected ResponseEntity<ErrorResponse> dateException(final Exception e) {
        log.error("dateException: {}", e.getMessage());
        return ResponseEntity
                .status(ErrorResponse.ErrorCode.DATETIME_PARSE_ERROR.getStatus().value())
                .body(new ErrorResponse(ErrorResponse.ErrorCode.DATETIME_PARSE_ERROR));
    }

//    // 좌표 업음 에러
//    @ExceptionHandler(NumberFormatException.class)
//    protected ResponseEntity<ErrorResponse> coordinateException(final Exception e) {
//        log.error("dateException: {}", e.getMessage());
//        return ResponseEntity
//                .status(ErrorResponse.ErrorCode.COORDINATE_EMPTY_ERROR.getStatus().value())
//                .body(new ErrorResponse(ErrorResponse.ErrorCode.COORDINATE_EMPTY_ERROR));
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("dtoException: {}", e.getMessage());
        return ResponseEntity
                .status(ErrorResponse.ErrorCode.DTO_NOT_VALID_ERROR.getStatus().value())
                .body(new ErrorResponse(ErrorResponse.ErrorCode.DTO_NOT_VALID_ERROR));
    }
}