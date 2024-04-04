package com.justdo.plug.member.domain.member.controller;

import com.justdo.plug.member.domain.member.dto.request.MemberInfoRequest;
import com.justdo.plug.member.domain.member.dto.response.MemberInfoResponse;
import com.justdo.plug.member.domain.member.service.MemberService;
import com.justdo.plug.member.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member/info")
@Slf4j

public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ApiResponse<MemberInfoResponse> getMyInfo(HttpServletRequest request) {

        String accessToken = request.getHeader("Authorization");

        MemberInfoResponse memberInfo = memberService.getInfo(accessToken);

        return ApiResponse.onSuccess(memberInfo);
    }

    @PutMapping
    public ApiResponse<MemberInfoResponse> updateMyInfo(HttpServletRequest request, @RequestBody MemberInfoRequest memberInfoRequest){
        String accessToken = request.getHeader("Authorization");

        MemberInfoResponse memberInfo = memberService.updateInfo(accessToken,memberInfoRequest);

        return ApiResponse.onSuccess(memberInfo);
    }

}
