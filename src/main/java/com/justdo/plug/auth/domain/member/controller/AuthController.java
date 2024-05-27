package com.justdo.plug.auth.domain.member.controller;

import com.justdo.plug.auth.domain.member.dto.MemberInfoRequest;
import com.justdo.plug.auth.domain.member.dto.MemberInfoResponse;
import com.justdo.plug.auth.domain.member.service.MemberService;
import com.justdo.plug.auth.global.jwt.kakao.preVersion.dto.JwtTokenResponse;
import com.justdo.plug.auth.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auths")
@Slf4j
@Tag(name = "Auth 관련 API입니다.")
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/login")
    @Operation(summary = "로그인 페이지 - 카카오 로그인/회원가입을 수행합니다.",
            description = "프론트 측에서 카카오로부터 전달받은 인가코드를 통해 카카오를 통한 소셜 로그인/회원가입을 진행합니다." +
                    "응답에는 JWT access token, refresh token이 포함됩니다.")
    public ApiResponse<JwtTokenResponse> kakaoLogin(@RequestParam String code) {
        return ApiResponse.onSuccess(memberService.processKakaoLogin(code));
    }

    @GetMapping
    @Operation(summary = "Open Feign 요청입니다 - 로그인 한 유저 정보를 조회합니다.",
            description = "Authorization 헤더의 access token 값을 기반으로" +
                    "현재 로그인한 유저의 정보를 조회합니다. ")
    public MemberInfoResponse getMyInfo(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        return memberService.getMemberInfo(accessToken);
    }

    @Operation(summary = "Open Feign 요청입니다 - 블로그 주인의 닉네임을 조회합니다.",
            description = "memberId를 요청받아 특정 블로그 주인의 닉네임을 조회합니다.")
    @GetMapping("/blogs/{memberId}")
    public String getMemberName(@PathVariable Long memberId) {

        return memberService.findMemberName(memberId);
    }

    @PutMapping
    @Operation(summary = "마이페이지(수정) - 현재 로그인 한 유저의 정보를 수정합니다.",
            description = "Authorization 헤더의 access token 값을 기반으로"
                    + "현재 로그인한 유저의 정보를 수정합니다.")
    public ApiResponse<MemberInfoResponse> updateMyInfo(HttpServletRequest request
            , @RequestBody MemberInfoRequest memberInfoRequest) {
        String accessToken = request.getHeader("Authorization");
        return ApiResponse.onSuccess(
                memberService.updateMemberInfo(accessToken, memberInfoRequest));
    }

    @PostMapping("/logout")
    @Operation(summary = "마이페이지 - 현재 로그인 한 유저의 로그아웃을 수행합니다.",
            description = "Authorization 헤더의 access token 값을 기반으로" +
                    "현재 로그인한 유저의 로그아웃을 수행합니다.")
    public ApiResponse<Object> logout(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        memberService.logout(accessToken);

        return ApiResponse.onSuccess(null);
    }


    @PostMapping("/reissue")
    @Operation(summary = "페이지 X - 현재 로그인 한 유저의 access token을 재발급합니다.",
            description =
                    "Authorization 헤더의 access token 값과 Authorization-refresh refresh token 값을 기반으로"
                            +
                            "현재 로그인한 유저의 access token을 재발급합니다.")
    public ApiResponse<Object> refreshAccessToken(HttpServletRequest request,
            HttpServletResponse response) {
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("Authorization-refresh");

        String newAccessToken = memberService.reissueAccessToken(accessToken, refreshToken);

        response.setHeader("Authorization", newAccessToken);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/blogs")
    @Operation(summary = "Open Feign 요청입니다 - 유저 리스트에서 유저 닉네임 리스트를 조회합니다.",
            description = "요청에 담긴 유저 리스트로 유저 닉네임 리스트를 조회합니다.")
    public List<String> getMemberNicknames(@RequestBody List<Long> memberIdList) {
        return memberService.getMemberNicknames(memberIdList);
    }
}
