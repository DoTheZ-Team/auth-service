package com.justdo.plug.member.domain.member;

import com.justdo.plug.member.domain.common.BaseTimeEntity;
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
    @Column(name = "member_id")
    private Long id;

    @Column(length = 20)
    private String name;

    @Column(length = 20)
    private String email;

    @Column(length = 10)
    private String socialType;

    @Column(length = 20)
    private String nickname;

    private String profile_url;

    private Boolean state;

    private LocalDateTime inactive_date;

    @Builder
    public Member(Long id,String email, String profile_url, String nickname) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profile_url = profile_url;
    }

    // 일단 양방향 연관관계 X
}
