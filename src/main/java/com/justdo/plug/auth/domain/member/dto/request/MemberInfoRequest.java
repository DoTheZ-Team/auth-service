package com.justdo.plug.auth.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberInfoRequest {
    @Schema(description = "유저 이메일",example = "ht0729@gachon.ac.kr")
    private String email;
    @Schema(description = "유저 닉네임",example = "정성실")
    private String nickname;
    @Schema(description = "유저 프로필 url",example = "https://xxx.xxx.asdf")
    private String profileUrl;

    @Builder
    public MemberInfoRequest(String email, String nickname, String profileUrl) {
        this.email = email;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }
}
