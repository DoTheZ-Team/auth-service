package com.justdo.plug.auth.domain.member;

import com.justdo.plug.auth.domain.common.BaseTimeEntity;
import com.justdo.plug.auth.domain.member.dto.MemberInfoRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column
    private String nickname;


    @Builder
    public Member(Long providerId, String provider, String email, String profileUrl,
            String nickname) {
        this.providerId = providerId;
        this.provider = provider;
        this.email = email;
        this.nickname = nickname;
    }

    public void updateMember(MemberInfoRequest request) {
        this.email = request.getEmail();
        this.nickname = request.getNickname();
    }

}
