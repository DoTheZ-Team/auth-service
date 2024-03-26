package com.justdo.plug.member.global.utils.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 900_000; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 86_400_000; // 1 day

    public String generateAccessToken(Long userId) {
        Date expiryDate = new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME);

        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());

        return Jwts.builder()
                .subject("accessToken")
                .expiration(expiryDate)
                .claim("userId", userId)
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken() {
        Date expiryDate = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME);

        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());

        return Jwts.builder()
                .subject("refreshToken")
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
            SecretKey secretKey = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());

            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            // 유효하지 않은 토큰이므로 false 반환
            return false;
        }
    }
}
