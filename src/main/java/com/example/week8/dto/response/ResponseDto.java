package com.example.week8.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {
    private boolean success;
    private T data;

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(true, data);
    }

    public static <T> ResponseDto<T> fail(T data) {
        return new ResponseDto<>(false, data);
    }

    // 프론트에서 아마 사용하지 말자고 할 것 같음 확인필요함.
    // 기존 개발은 프론트에서 요청한대로 현재형식대로 구현하고 추후 상태코드와 함께 반환하는 방식으로 확장할 필요성이 있음.
    @Getter
    @AllArgsConstructor
    static class Error {
        private String code;
        private String message;
    }

}

