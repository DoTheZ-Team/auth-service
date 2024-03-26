package com.justdo.plug.member.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtTokenResponse {

    private final String accessToken;
    private final String refreshToken;

    @Builder
    public JwtTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
