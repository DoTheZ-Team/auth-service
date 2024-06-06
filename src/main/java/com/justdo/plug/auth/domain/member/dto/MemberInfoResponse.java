package com.justdo.plug.auth.domain.member.dto;

import com.justdo.plug.auth.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberInfoResponse {

    @Schema(description = "유저 이메일", example = "ht0729@gachon.ac.kr")
    private String email;

    @Schema(description = "유저 닉네임", example = "정성실")
    private String nickname;

    @Builder
    public MemberInfoResponse(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public static MemberInfoResponse toMemberInfoResponse(Member member) {
        return MemberInfoResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}
