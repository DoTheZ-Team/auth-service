package com.justdo.plug.member.global.config;

import com.justdo.plug.member.domain.member.Member;
import com.justdo.plug.member.domain.member.dto.kakao.KakaoUserInfo;
import com.justdo.plug.member.domain.member.repository.MemberRepository;
import com.justdo.plug.member.global.exception.ApiException;
import com.justdo.plug.member.global.response.code.status.ErrorStatus;
import com.justdo.plug.member.global.utils.jwt.JwtTokenProvider;
import com.justdo.plug.member.global.utils.jwt.RedisUtils;
import io.lettuce.core.RedisConnectionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.justdo.plug.member.global.utils.jwt.JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_TIME;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtils redisUtils;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        // 로그인 or 회원가입
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(defaultOAuth2User.getAttributes());

        Long userId = registerIfNewUser(kakaoUserInfo);

        String accessToken = jwtTokenProvider.generateAccessToken(userId);
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        storeRefreshTokenInRedis(userId,refreshToken);

        response.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        response.setHeader("Authorization-refresh", refreshToken);

    }
    private void storeRefreshTokenInRedis(Long userId, String refreshToken) {
        try {
            redisUtils.setData(userId.toString(),refreshToken,REFRESH_TOKEN_EXPIRATION_TIME);
        } catch (RedisConnectionException | DataAccessException e) {
            throw new ApiException(ErrorStatus._REDIS_OPERATION_ERROR);
        }
    }

    private Long registerIfNewUser(KakaoUserInfo kakaoUserInfo) {
        if (!memberRepository.existsByProviderId(kakaoUserInfo.getId())) {
            Member newMember = Member.builder()
                    .providerId(kakaoUserInfo.getId())
                    .email(kakaoUserInfo.getEmail())
                    .nickname(kakaoUserInfo.getNickname())
                    .profile_url(kakaoUserInfo.getProfileImageUrl())
                    .build();

            Member savedMember = memberRepository.save(newMember);
            return savedMember.getId();
        }
        else {
            Member foundMember = memberRepository.findByProviderId(kakaoUserInfo.getId());
            return foundMember.getId();
        }
    }
}
