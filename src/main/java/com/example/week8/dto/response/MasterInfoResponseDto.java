package com.example.week8.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MasterInfoResponseDto {
    private Long id;
    private String nickname;
}
