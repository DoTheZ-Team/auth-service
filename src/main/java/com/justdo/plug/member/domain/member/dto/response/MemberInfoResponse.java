package com.justdo.plug.member.domain.member.dto.response;

import com.justdo.plug.member.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberInfoResponse {
    private String email;
    private String nickname;
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
