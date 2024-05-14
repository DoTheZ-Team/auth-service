package com.justdo.plug.auth.global.jwt.kakao.preVersion.dto;

import lombok.Getter;

@Getter
public class KakaoAccount {
    private boolean profile_nickname_needs_agreement;
    private boolean profile_image_needs_agreement;
    private KakaoProfile profile;
    private boolean has_email;
    private boolean email_needs_agreement;
    private boolean is_email_valid;
    private boolean is_email_verified;
    private String email;
}
