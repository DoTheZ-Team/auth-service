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

    // LOGIN TODO
    // 1. S3 프로필 이미지 저장

    @GetMapping("/login")
    public ApiResponse<JwtTokenResponse> kakaoLogin(@RequestParam String code) {

        JwtTokenResponse response = memberService.processKakaoLogin(code);

        return ApiResponse.onSuccess(response);
    }
}
