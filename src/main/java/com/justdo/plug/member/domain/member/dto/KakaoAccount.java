package com.justdo.plug.member.domain.member.dto;

import lombok.Data;

@Data
public class KakaoAccount {
    private boolean profileNicknameNeedsAgreement;
    private boolean profileImageNeedsAgreement;
    private KakaoProfile profile;
    private boolean hasEmail;
    private boolean emailNeedsAgreement;
    private boolean isEmailValid;
    private boolean isEmailVerified;
    private String email;
}
