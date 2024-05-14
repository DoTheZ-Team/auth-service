package com.justdo.plug.auth.global.jwt.kakao.preVersion.dto;

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

    public static JwtTokenResponse createJwtTokenResponse(String accessToken, String refreshToken) {
        return JwtTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
