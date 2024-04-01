package com.justdo.plug.member.domain.member.service;

import com.justdo.plug.member.domain.member.Member;
import com.justdo.plug.member.domain.member.dto.kakao.KakaoAccount;
import com.justdo.plug.member.domain.member.dto.kakao.KakaoProfile;
import com.justdo.plug.member.domain.member.dto.response.JwtTokenResponse;
import com.justdo.plug.member.domain.member.dto.kakao.KakaoTokenResponse;
import com.justdo.plug.member.domain.member.dto.kakao.KakaoUserInfoResponse;
import com.justdo.plug.member.domain.member.repository.MemberRepository;
import com.justdo.plug.member.global.exception.ApiException;
import com.justdo.plug.member.global.response.code.status.ErrorStatus;
import com.justdo.plug.member.global.utils.kakao.KakaoTokenJsonData;
import com.justdo.plug.member.global.utils.kakao.KakaoUserInfo;
import com.justdo.plug.member.global.utils.jwt.JwtTokenProvider;
import io.lettuce.core.RedisConnectionException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.justdo.plug.member.global.utils.jwt.JwtTokenProvider.REFRESH_TOKEN_EXPIRATION_TIME;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;



    private final KakaoUserInfo kakaoUserInfo;
    private final KakaoTokenJsonData kakaoTokenJsonData;

    private final JwtTokenProvider jwtTokenProvider;

    private final RedisTemplate<String, String> redisTemplate;

    private boolean existsById(Long id){
        return memberRepository.existsById(id);
    }

    public JwtTokenResponse processKakaoLogin(String code) {
        KakaoTokenResponse tokenResponse = getTokenResponseFromKakao(code);
        KakaoUserInfoResponse userInfo = getUserInfoFromKakao(tokenResponse.getAccess_token());
        boolean isNew = registerUserIfNew(userInfo);

        String accessToken = jwtTokenProvider.generateAccessToken(userInfo.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        storeRefreshTokenInRedis(userInfo.getId(), refreshToken);

        return createJwtTokenResponse(accessToken, refreshToken, isNew);
    }

    private KakaoTokenResponse getTokenResponseFromKakao(String code) {
        return kakaoTokenJsonData.getToken(code);
    }

    private KakaoUserInfoResponse getUserInfoFromKakao(String accessToken) {
        return kakaoUserInfo.getUserInfo(accessToken);
    }

    private boolean registerUserIfNew(KakaoUserInfoResponse userInfo) {
        if (!existsById(userInfo.getId())) {
            Member newMember = createNewMember(userInfo);
            memberRepository.save(newMember);
            return true;
        }
        return false;
    }

    private Member createNewMember(KakaoUserInfoResponse userInfo) {
        KakaoAccount kakaoAccount = userInfo.getKakao_account();
        KakaoProfile kakaoProfile = kakaoAccount.getProfile();
        return Member.builder()
                .id(userInfo.getId())
                .email(kakaoAccount.getEmail())
                .nickname(kakaoProfile.getNickname())
                .profile_url(kakaoProfile.getProfile_image_url())
                .phone_share_state(false)
                .build();
    }

    private void storeRefreshTokenInRedis(Long userId, String refreshToken) {
        try {
            redisTemplate.opsForValue().set(String.valueOf(userId), refreshToken,
                    REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
        } catch (RedisConnectionException | DataAccessException e) {
            throw new ApiException(ErrorStatus._REDIS_OPERATION_ERROR);
        }
    }

    private JwtTokenResponse createJwtTokenResponse(String accessToken, String refreshToken, boolean isNew) {
        return JwtTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNew(isNew)
                .build();
    }

}
