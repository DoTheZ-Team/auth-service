package com.justdo.plug.member.domain.member.controller;

import com.justdo.plug.member.domain.member.Member;
import com.justdo.plug.member.domain.member.dto.JwtTokenResponse;
import com.justdo.plug.member.domain.member.dto.KakaoTokenResponse;
import com.justdo.plug.member.domain.member.dto.KakaoUserInfoResponse;
import com.justdo.plug.member.domain.member.service.MemberService;
import com.justdo.plug.member.global.response.ApiResponse;
import com.justdo.plug.member.global.utils.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    private final JwtTokenProvider jwtTokenProvider;

    // 추가사항
    // 1. 비즈니스 로직 서비스로 이동
    // 2. S3 프로필 이미지 저장
    // 3. refresh 토큰 redis에 저장

    @GetMapping("/login")
    public ApiResponse<JwtTokenResponse> kakaoLogin(@RequestParam String code) {

        KakaoTokenResponse kakaoTokenJsonData = memberService.getKakaoTokenJsonData(code);

        KakaoUserInfoResponse kakaoUserInfo = memberService.getKakaoUserInfo(kakaoTokenJsonData.getAccess_token());

        if (!memberService.existsById(kakaoUserInfo.getId())){
            Member newMember = Member.builder()
                    .id(kakaoUserInfo.getId())
                    .email(kakaoUserInfo.getKakao_account().getEmail())
                    .nickname(kakaoUserInfo.getKakao_account().getProfile().getNickname())
                    .profile_url(kakaoUserInfo.getKakao_account().getProfile().getProfile_image_url())
                    .phone_share_state(false)
                    .build();

            memberService.saveMember(newMember);
        }
        else{
            log.info("이미 회원이 존재합니다");
        }


        // JWT 토큰 발급 후 리턴
        String accessToken = jwtTokenProvider.generateAccessToken(kakaoUserInfo.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        JwtTokenResponse response = JwtTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();

        return ApiResponse.onSuccess(response);
    }
}
