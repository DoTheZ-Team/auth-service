package com.justdo.plug.member.domain.member.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AuthController {

    @GetMapping("/login")
    public String kakaoLogin(@RequestParam String code) {

        return "redirect:/";
    }
}
