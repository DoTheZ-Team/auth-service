package com.justdo.plug.auth.global.jwt.kakao;

import lombok.Getter;

import java.util.Map;

@Getter
public class KakaoUserInfo{

    private final Map<String, Object> attributes; // getAttributes
    private final Map<String, Object> attributesProperties; // getAttributes
    private final Map<String, Object> attributesAccount; // getAttributes
    private final Map<String, Object> attributesProfile; // getAttributes

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.attributesProperties = (Map<String, Object>) attributes.get("properties");
        this.attributesAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.attributesProfile = (Map<String, Object>) attributesAccount.get("profile");
    }

    public Long getId(){
        return (Long) attributes.get("id");
    }

    public String getNickname(){
        return (String) attributesProfile.get("nickname");
    }

    public String getEmail(){
        return (String) attributesAccount.get("email");
    }

    public String getProfileImageUrl(){
        return (String) attributesProfile.get("profile_image_url");
    }

}
