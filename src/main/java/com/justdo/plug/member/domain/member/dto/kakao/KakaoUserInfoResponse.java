package com.justdo.plug.member.domain.member.dto.kakao;

import lombok.Data;
import lombok.Getter;

@Getter
public class KakaoUserInfoResponse {
    private Long id;
    private String connectedAt;
    private KakaoProperties properties;
    private KakaoAccount kakao_account;
}
