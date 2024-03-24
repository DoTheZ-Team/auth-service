package com.justdo.plug.member.global.utils;

import com.justdo.plug.member.domain.member.dto.KakaoTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class KakaoTokenJsonData {
    private final WebClient webClient = WebClient.create();

    // 값 application.yml로 옮기기
    private static final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
    private static final String REDIRECT_URI = "https://localhost:3000/login";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String CLIENT_ID = "{secret.CLIENT_ID}";

    public KakaoTokenResponse getToken(String code) {
        String uri = TOKEN_URI + "?grant_type=" + GRANT_TYPE + "&client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&code=" + code;
        System.out.println(uri);

        Flux<KakaoTokenResponse> response = webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .bodyToFlux(KakaoTokenResponse.class);

        return response.blockFirst();
    }
}
