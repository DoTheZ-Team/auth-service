package com.justdo.plug.member.domain.member.dto;

import lombok.Data;

@Data
public class KakaoProfile {
    private String nickname;
    private String thumbnailImageUrl;
    private String profileImageUrl;
    private boolean isDefaultImage;
    private boolean isDefaultNickname;
}
