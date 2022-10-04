package com.example.week8.dto.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
public class InviteMemberDto {

    @NotBlank
    private String nickname;
}
