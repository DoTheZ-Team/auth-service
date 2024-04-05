package com.justdo.plug.member.domain.member.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberInfoRequest {
    private String email;
    private String nickname;
    private String profile_url;

    @Builder
    public MemberInfoRequest(String email, String nickname, String profile_url) {
        this.email = email;
        this.nickname = nickname;
        this.profile_url = profile_url;
    }
}
