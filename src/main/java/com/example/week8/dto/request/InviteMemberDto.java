package com.example.week8.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class InviteMemberDto {

    @NotBlank
    private String nickname;
}
