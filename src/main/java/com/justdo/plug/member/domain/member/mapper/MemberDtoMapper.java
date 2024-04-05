package com.justdo.plug.member.domain.member.mapper;

import com.justdo.plug.member.domain.member.Member;
import com.justdo.plug.member.domain.member.dto.response.MemberInfoResponse;

public class MemberDtoMapper {
    public static MemberInfoResponse mapMemberToMemberInfoResponse(Member member) {
        return MemberInfoResponse.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profile_url(member.getProfile_url())
                .build();
    }
}
