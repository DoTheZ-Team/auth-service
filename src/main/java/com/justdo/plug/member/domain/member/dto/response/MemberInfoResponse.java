package com.justdo.plug.member.domain.member.dto.response;

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
}
