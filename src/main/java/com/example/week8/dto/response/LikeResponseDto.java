package com.example.week8.dto.response;

import com.example.week8.domain.enums.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class LikeResponseDto {

    private final Long post_id;
    private final Category category;

    //프론트와 상의 후 attribute 추가 예정

}
