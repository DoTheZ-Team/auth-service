package com.justdo.plug.member.global.config;

import com.justdo.plug.member.domain.member.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;
    private final CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(config -> config.anyRequest().permitAll());

        http.oauth2Login((oauth2) -> oauth2
                .loginPage("/login")
                .successHandler(successHandler)
                .userInfoEndpoint(userInfoEndPoint -> userInfoEndPoint.userService(oAuth2UserService))
        );

        return http.build();
    }

}
