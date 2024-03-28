package com.justdo.plug.member.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtTokenResponse {

    private final String accessToken;
    private final String refreshToken;

    private final Boolean isNew;

    @Builder
    public JwtTokenResponse(String accessToken, String refreshToken, Boolean isNew) {
        this.isNew = isNew;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
