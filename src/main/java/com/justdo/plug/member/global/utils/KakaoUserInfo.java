package com.justdo.plug.member.global.utils;

import com.justdo.plug.member.domain.member.dto.KakaoUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
public class KakaoUserInfo {
    private final WebClient webClient = WebClient.create();

    @Value("${kakao.user_into_uri}")
    private String userInfoUri;

    public KakaoUserInfoResponse getUserInfo(String token) {

        Flux<KakaoUserInfoResponse> response = webClient.get()
                .uri(userInfoUri)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(KakaoUserInfoResponse.class);

        return response.blockFirst();
    }
}