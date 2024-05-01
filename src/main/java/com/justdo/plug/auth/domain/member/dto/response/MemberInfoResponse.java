package com.justdo.plug.auth.domain.member.dto.response;

import com.justdo.plug.auth.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberInfoResponse {
    @Schema(description = "유저 이메일",example = "ht0729@gachon.ac.kr")
    private String email;
    @Schema(description = "유저 닉네임",example = "정성실")
    private String nickname;
    @Schema(description = "유저 프로필 url",example = "https://xxx.xxx.asdf")
    private String profile_url;

    @Builder
    public MemberInfoResponse(String email, String nickname, String profile_url) {
        this.email = email;
        this.nickname = nickname;
        this.profile_url = profile_url;
    }

    public static MemberInfoResponse mapMemberToMemberInfoResponse(Member member) {
        return MemberInfoResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profile_url(member.getProfile_url())
                .build();
    }
}
