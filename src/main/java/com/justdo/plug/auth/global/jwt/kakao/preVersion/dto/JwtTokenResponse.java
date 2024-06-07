package com.justdo.plug.auth.global.jwt.kakao.preVersion.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtTokenResponse {

    private final String accessToken;
    private final String refreshToken;
    private final Long blogId;

    @Builder
    public JwtTokenResponse(String accessToken, String refreshToken, Long blogId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.blogId = blogId;
    }

    public static JwtTokenResponse createJwtTokenResponse(String accessToken, String refreshToken,Long blogId) {
        return JwtTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .blogId(blogId)
                .build();
    }
}
