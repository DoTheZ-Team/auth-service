package com.justdo.plug.member.domain.member.service;

import com.justdo.plug.member.domain.member.Member;
import com.justdo.plug.member.domain.member.dto.JwtTokenResponse;
import com.justdo.plug.member.domain.member.dto.KakaoTokenResponse;
import com.justdo.plug.member.domain.member.dto.KakaoUserInfoResponse;
import com.justdo.plug.member.domain.member.repository.MemberRepository;
import com.justdo.plug.member.global.utils.KakaoTokenJsonData;
import com.justdo.plug.member.global.utils.KakaoUserInfo;
import com.justdo.plug.member.global.utils.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.justdo.plug.member.global.utils.jwt.JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_TIME;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final KakaoUserInfo kakaoUserInfo;
    private final KakaoTokenJsonData kakaoTokenJsonData;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public boolean existsById(Long id){
        return memberRepository.existsById(id);
    }

    public JwtTokenResponse processKakaoLogin(String code) {
        KakaoTokenResponse tokenResponse = kakaoTokenJsonData.getToken(code);
        KakaoUserInfoResponse userInfo = kakaoUserInfo.getUserInfo(tokenResponse.getAccess_token());

        handleUserRegistration(userInfo);

        String accessToken = jwtTokenProvider.generateAccessToken(userInfo.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        redisTemplate.opsForValue().set(String.valueOf(userInfo.getId()), refreshToken,
                REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);

        return JwtTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }


    private void handleUserRegistration(KakaoUserInfoResponse userInfo) {
        if (!existsById(userInfo.getId())) {
            Member newMember = Member.builder()
                    .id(userInfo.getId())
                    .email(userInfo.getKakao_account().getEmail())
                    .nickname(userInfo.getKakao_account().getProfile().getNickname())
                    .profile_url(userInfo.getKakao_account().getProfile().getProfile_image_url())
                    .phone_share_state(false)
                    .build();

            memberRepository.save(newMember);
        }
    }

}
