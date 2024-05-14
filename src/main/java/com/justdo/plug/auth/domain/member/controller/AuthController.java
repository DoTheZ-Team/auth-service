package com.justdo.plug.auth.domain.member.controller;

import com.justdo.plug.auth.domain.member.dto.request.MemberInfoRequest;
import com.justdo.plug.auth.domain.member.dto.response.MemberInfoResponse;
import com.justdo.plug.auth.domain.member.service.MemberService;
import com.justdo.plug.auth.global.jwt.kakao.preVersion.dto.JwtTokenResponse;
import com.justdo.plug.auth.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auths")
@Slf4j
@Tag(name = "AuthController")
public class AuthController {

    private final MemberService memberService;

    @GetMapping("/login")
    @Operation(summary = "카카오 로그인", description = "전달받은 인가코드를 통해 카카오 로그인을 수행한다.")
    public ApiResponse<JwtTokenResponse> kakaoLogin(@RequestParam String code) {
        return ApiResponse.onSuccess(memberService.processKakaoLogin(code));
    }

    @GetMapping
    @Operation(summary = "로그인 한 유저 정보 조회", description = "현재 로그인한 유저의 정보를 조회한다.")
    public MemberInfoResponse getMyInfo(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        return memberService.getMemberInfo(accessToken);
    }

    @Operation(summary = "블로그 주인의 닉네임을 조회합니다. ", description = "Open feign 요청입니다.")
    @GetMapping("/blogs/{memberId}")
    public String getMemberName(@PathVariable Long memberId) {

        return memberService.findMemberName(memberId);
    }

    @PutMapping
    @Operation(summary = "로그인 한 유저 정보 수정", description = "현재 로그인한 유저의 정보를 수정한다.")
    public ApiResponse<MemberInfoResponse> updateMyInfo(HttpServletRequest request
            , @RequestBody MemberInfoRequest memberInfoRequest) {
        String accessToken = request.getHeader("Authorization");
        return ApiResponse.onSuccess(memberService.updateMemberInfo(accessToken,memberInfoRequest));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그인 한 유저 로그아웃", description = "현재 로그인한 유저의 로그아웃을 수행한다.")
    public void logout(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        memberService.logout(accessToken);
    }


    @PostMapping("/reissue")
    @Operation(summary = "로그인 한 유저 access 토큰 재발급", description = "현재 로그인한 유저의 access 토큰을 재발급한다.")
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("Authorization-refresh");

        String newAccessToken = memberService.reissueAccessToken(accessToken, refreshToken);

        response.setHeader("Authorization", newAccessToken);
    }

    @PostMapping("/blogs")
    @Operation(summary = "유저 리스트에서 유저 닉네임 리스트 조회", description = "요청에 담긴 유저 리스트로 유저 닉네임 리스트를 조회한다.")
    public List<String> getMemberNicknames(@RequestBody List<Long> memberIdList) {
        return memberService.getMemberNicknames(memberIdList);
    }
}
