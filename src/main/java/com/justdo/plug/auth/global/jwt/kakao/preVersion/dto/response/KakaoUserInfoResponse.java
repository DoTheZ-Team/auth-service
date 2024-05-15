package com.justdo.plug.auth.global.jwt.kakao.preVersion.dto.response;


import com.justdo.plug.auth.global.jwt.kakao.preVersion.dto.KakaoAccount;
import com.justdo.plug.auth.global.jwt.kakao.preVersion.dto.KakaoProperties;
import lombok.Getter;
@Getter
public class KakaoUserInfoResponse {
    private Long id;
    private String connectedAt;
    private KakaoProperties properties;
    private KakaoAccount kakao_account;
}
