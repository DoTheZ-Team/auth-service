package com.justdo.plug.member.domain.member;

import com.justdo.plug.member.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 10)
    private String name;

    @Column(length = 20)
    private String email;

    @Column(length = 10)
    private String socialType;

    @Column(length = 20)
    private String nickname;

    @Lob
    private String profile_url;

    private Boolean state;

    @Column(nullable = false)
    private Boolean phone_share_state;

    private Boolean match_state;

    private LocalDateTime inactive_date;

    // 일단 양방향 연관관계 X
}
