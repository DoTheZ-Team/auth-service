package com.justdo.plug.member.domain.member.service;

import com.justdo.plug.member.domain.member.Member;
import com.justdo.plug.member.domain.member.dto.request.MemberInfoRequest;
import com.justdo.plug.member.domain.member.dto.response.MemberInfoResponse;
import com.justdo.plug.member.domain.member.repository.MemberRepository;
import com.justdo.plug.member.global.exception.ApiException;
import com.justdo.plug.member.global.jwt.JwtTokenProvider;
import com.justdo.plug.member.global.response.code.status.ErrorStatus;
import com.justdo.plug.member.global.utils.redis.RedisUtils;
import io.lettuce.core.RedisConnectionException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtils redisUtils;


    // 멤버 정보 조회
    public MemberInfoResponse getMemberInfo(String accessToken) {
        checkToken(accessToken);

        Long userId = extractUserIdFromToken(accessToken);

        Member foundmMember = findMember(userId);

        return MemberInfoResponse.mapMemberToMemberInfoResponse(foundmMember);
    }

    // 멤버 정보 수정
    @Transactional
    public MemberInfoResponse updateMemberInfo(String accessToken,
        MemberInfoRequest memberInfoRequest) {
        checkToken(accessToken);

        Long userId = extractUserIdFromToken(accessToken);

        Member foundMember = findMember(userId);

        foundMember.updateMember(memberInfoRequest);

        return MemberInfoResponse.mapMemberToMemberInfoResponse(foundMember);
    }

    // 로그아웃
    @Transactional
    public void logout(String accessToken) {
        deleteRefreshToken(accessToken);
    }

    // 액세스 토큰 재발급
    @Transactional
    public String reissueAccessToken(String accessToken, String refreshToken) {
        checkToken(accessToken);

        validateRefreshToken(refreshToken);
        Long userId = extractUserIdFromToken(accessToken);

        validateStoredRefreshToken(userId.toString(), refreshToken);

        return jwtTokenProvider.generateAccessToken(userId);
    }

    private void validateRefreshToken(String refreshToken) {
        if (!jwtTokenProvider.isTokenValid(refreshToken)) {
            throw new ApiException(ErrorStatus._INVALID_REFRESH_TOKEN);
        }
    }

    private void validateStoredRefreshToken(String userId, String refreshToken) {
        String storedRefreshToken = redisUtils.getData(userId);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new ApiException(ErrorStatus._INVALID_REFRESH_TOKEN);
        }
    }


    private void deleteRefreshToken(String accessToken) {
        checkToken(accessToken);

        Long userId = extractUserIdFromToken(accessToken);

        try {
            redisUtils.deleteData(userId.toString());
        } catch (RedisConnectionException | DataAccessException e) {
            throw new ApiException(ErrorStatus._REDIS_OPERATION_ERROR);
        }

    }

    private Member findMember(Long userId) {
        return memberRepository.findById(userId).orElseThrow(
            () -> new ApiException(ErrorStatus._MEMBER_NOT_FOUND_ERROR));
    }

    private void checkToken(String accessToken) {
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            throw new ApiException(ErrorStatus._UNAUTHORIZED);
        }
    }

    private Long extractUserIdFromToken(String jwtToken) {
        String jwt = jwtToken.substring(7);

        Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

        if (userId == null) {
            throw new ApiException(ErrorStatus._UNAUTHORIZED);
        }

        return userId;
    }

    public List<String> getMemberNicknames(List<Long> memberIdList) {

        return memberRepository.findAllMemberIdList(memberIdList).stream()
            .map(Member::getNickname)
            .toList();
    }
}
