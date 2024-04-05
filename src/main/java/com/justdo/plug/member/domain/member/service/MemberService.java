package com.justdo.plug.member.domain.member.service;

import com.justdo.plug.member.domain.member.Member;
import com.justdo.plug.member.domain.member.dto.request.MemberInfoRequest;
import com.justdo.plug.member.domain.member.dto.response.MemberInfoResponse;
import com.justdo.plug.member.domain.member.repository.MemberRepository;
import com.justdo.plug.member.global.exception.ApiException;
import com.justdo.plug.member.global.response.code.status.ErrorStatus;
import com.justdo.plug.member.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberInfoResponse getInfo(String accessToken){
        checkToken(accessToken);

        Long userId = extractUserIdFromToken(accessToken);

        Member foundmMember = findMember(userId);

        return MemberInfoResponse.mapMemberToMemberInfoResponse(foundmMember);
    }

    @Transactional
    public MemberInfoResponse updateInfo(String accessToken, MemberInfoRequest memberInfoRequest){
        checkToken(accessToken);

        Long userId = extractUserIdFromToken(accessToken);

        Member foundmMember = findMember(userId);

        foundmMember.updateMember(memberInfoRequest);

        return MemberInfoResponse.mapMemberToMemberInfoResponse(foundmMember);
    }

    private Member findMember(Long userId){
        return memberRepository.findById(userId).orElseThrow(
                () -> new ApiException(ErrorStatus._MEMBER_NOT_FOUND_ERROR));
    }

    private void checkToken(String accessToken){
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            throw new ApiException(ErrorStatus._UNAUTHORIZED);
        }
    }

    private Long extractUserIdFromToken(String jwtToken) {
        String jwt = jwtToken.substring(7);

        Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

        if (userId == null){
            throw new ApiException(ErrorStatus._UNAUTHORIZED);
        }

        return jwtTokenProvider.getUserIdFromToken(jwt);
    }

}
