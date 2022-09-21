package com.example.week8.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuplicationRequestDto {
    private String value;

    public DuplicationRequestDto(String newValue) {
        this.value = newValue;
    }
}
