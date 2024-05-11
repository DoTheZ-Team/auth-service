package com.justdo.plug.auth.domain.member.service;

import com.justdo.plug.auth.domain.member.Member;
import com.justdo.plug.auth.domain.member.dto.request.MemberInfoRequest;
import com.justdo.plug.auth.domain.member.dto.response.MemberInfoResponse;
import com.justdo.plug.auth.domain.member.repository.MemberRepository;
import com.justdo.plug.auth.global.exception.ApiException;
import com.justdo.plug.auth.global.jwt.JwtTokenProvider;
import com.justdo.plug.auth.global.response.code.status.ErrorStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;


    // 멤버 정보 조회
    public MemberInfoResponse getMemberInfo(String accessToken) {
        jwtTokenProvider.checkToken(accessToken);

        Long userId = jwtTokenProvider.extractUserIdFromToken(accessToken);

        Member foundmMember = findMember(userId);

        return MemberInfoResponse.mapMemberToMemberInfoResponse(foundmMember);
    }

    // 멤버 정보 수정
    @Transactional
    public MemberInfoResponse updateMemberInfo(String accessToken,
        MemberInfoRequest memberInfoRequest) {
        jwtTokenProvider.checkToken(accessToken);

        Long userId = jwtTokenProvider.extractUserIdFromToken(accessToken);

        Member foundMember = findMember(userId);

        foundMember.updateMember(memberInfoRequest);

        return MemberInfoResponse.mapMemberToMemberInfoResponse(foundMember);
    }

    // 로그아웃
    @Transactional
    public void logout(String accessToken) {
        jwtTokenProvider.deleteRefreshToken(accessToken);
    }

    // 액세스 토큰 재발급
    @Transactional
    public String reissueAccessToken(String accessToken, String refreshToken) {
        jwtTokenProvider.checkToken(accessToken);

        jwtTokenProvider.validateRefreshToken(refreshToken);
        Long userId = jwtTokenProvider.extractUserIdFromToken(accessToken);

        jwtTokenProvider.validateStoredRefreshToken(userId.toString(), refreshToken);

        return jwtTokenProvider.generateAccessToken(userId);
    }

    // 유저 리스트에서 유저 닉네임 리스트 조회
    public List<String> getMemberNicknames(List<Long> memberIdList) {

        return memberRepository.findAllMemberIdList(memberIdList).stream()
            .map(Member::getNickname)
            .toList();
    }

    // 특정 멤버 검색
    private Member findMember(Long userId) {
        return memberRepository.findById(userId).orElseThrow(
            () -> new ApiException(ErrorStatus._MEMBER_NOT_FOUND_ERROR));
    }

    public String findMemberName(Long memberId) {
        return findMember(memberId).getNickname();
    }

}
