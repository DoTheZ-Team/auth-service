package com.justdo.plug.member.domain.member.dto;

import lombok.Data;

@Data
public class KakaoUserInfoResponse {
    private Long id;
    private String connectedAt;
    private KakaoProperties properties;
    private KakaoAccount kakaoAccount;
}
