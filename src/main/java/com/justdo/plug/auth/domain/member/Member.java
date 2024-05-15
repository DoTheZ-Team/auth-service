package com.justdo.plug.auth.domain.member;

import com.justdo.plug.auth.domain.common.BaseTimeEntity;
import com.justdo.plug.auth.domain.member.dto.request.MemberInfoRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "provider_id")
    private Long providerId;

    @Column(length = 100)
    private String email;

    @Column(length = 10)
    private String provider;

    @Column(length = 20)
    private String nickname;

    private String profileUrl;


    @Builder
    public Member(Long providerId,String email, String profile_url, String nickname) {
        this.providerId = providerId;
        this.nickname = nickname;
        this.email = email;
        this.profileUrl = profileUrl;
    }

    public void updateMember(MemberInfoRequest request){
        this.email = request.getEmail();
        this.profileUrl = request.getProfileUrl();
        this.nickname = request.getNickname();
    }

}
