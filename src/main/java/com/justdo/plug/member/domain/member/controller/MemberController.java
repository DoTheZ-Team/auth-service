package com.justdo.plug.member.domain.member.controller;

import com.justdo.plug.member.domain.member.dto.request.MemberInfoRequest;
import com.justdo.plug.member.domain.member.dto.response.MemberInfoResponse;
import com.justdo.plug.member.domain.member.service.MemberService;
import com.justdo.plug.member.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auths")
@Slf4j

public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public MemberInfoResponse getMyInfo(HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization");

        return memberService.getMemberInfo(accessToken);
    }

    @PutMapping
    public ApiResponse<MemberInfoResponse> updateMyInfo(HttpServletRequest request,
        @RequestBody MemberInfoRequest memberInfoRequest) {
        String accessToken = request.getHeader("Authorization");

        MemberInfoResponse memberInfo = memberService.updateMemberInfo(accessToken,
            memberInfoRequest);

        return ApiResponse.onSuccess(memberInfo);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");

        memberService.logout(accessToken);
    }


    @PostMapping("/reissue")
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("Authorization-refresh");

        String newAccessToken = memberService.reissueAccessToken(accessToken, refreshToken);

        response.setHeader("Authorization", newAccessToken);
    }

    @PostMapping("/blogs")
    public List<String> getMemberNicknames(@RequestBody List<Long> memberIdList) {

        return memberService.getMemberNicknames(memberIdList);
    }
}
