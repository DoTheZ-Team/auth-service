package com.justdo.plug.auth.global.jwt.kakao;

import com.justdo.plug.auth.domain.blog.BlogClient;
import com.justdo.plug.auth.domain.member.Member;
import com.justdo.plug.auth.domain.member.repository.MemberRepository;
import com.justdo.plug.auth.global.exception.ApiException;
import com.justdo.plug.auth.global.jwt.JwtTokenProvider;
import com.justdo.plug.auth.global.response.code.status.ErrorStatus;
import com.justdo.plug.auth.global.utils.redis.RedisUtils;
import io.lettuce.core.RedisConnectionException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static com.justdo.plug.auth.global.jwt.JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_TIME;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtils redisUtils;
    private final MemberRepository memberRepository;
    private final BlogClient blogClient;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        // 로그인 or 회원가입
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(defaultOAuth2User.getAttributes());

        Long userId = registerIfNewUser(kakaoUserInfo);

        String accessToken = jwtTokenProvider.generateAccessToken(userId);
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        storeRefreshTokenInRedis(userId,refreshToken);

        Cookie accessCookie = new Cookie("accessToken",accessToken);
        Cookie refreshCookie = new Cookie("refreshToken",refreshToken);

        accessCookie.setPath("/");
        refreshCookie.setPath("/");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        // 로그인/회원가입 후 front url
        response.sendRedirect("http://localhost:3000/home");

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

            blogClient.createBlog(savedMember.getId());

            return savedMember.getId();
        }
        else {
            Optional<Member> foundMember = memberRepository.findByProviderId(kakaoUserInfo.getId());
            return foundMember.orElseThrow(() -> new ApiException(ErrorStatus._MEMBER_NOT_FOUND_ERROR)).getId();
        }
    }
}
