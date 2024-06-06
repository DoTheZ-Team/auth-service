package com.justdo.plug.auth.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberInfoRequest {

    @Schema(description = "유저 이메일", example = "ht0729@gachon.ac.kr")
    private String email;
    @Schema(description = "유저 닉네임", example = "정성실")
    private String nickname;

    @Builder
    public MemberInfoRequest(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
