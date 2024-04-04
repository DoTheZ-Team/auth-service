package com.justdo.plug.member.global.utils.jwt;


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

    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 15 * 60 * 1000; // 15분
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7일

    private SecretKey getSecretKey() {
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        return Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }

    private Date getExpiryDate(long expirationTime) {
        return new Date(System.currentTimeMillis() + expirationTime);
    }

    public String generateAccessToken(Long userId) {
        Date expiryDate = getExpiryDate(ACCESS_TOKEN_EXPIRATION_TIME);
        SecretKey secretKey = getSecretKey();

        return Jwts.builder()
                .subject("accessToken")
                .expiration(expiryDate)
                .claim("userId", userId)
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken() {
        Date expiryDate = getExpiryDate(REFRESH_TOKEN_EXPIRATION_TIME);
        SecretKey secretKey = getSecretKey();

        return Jwts.builder()
                .subject("refreshToken")
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    // New method to extract userId claim
    public Long getUserIdFromToken(String token) {
        try {
            SecretKey secretKey = getSecretKey();

            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId",Long.class);

        } catch (JwtException e) {
            return null;
        }
    }


    // 코드 수정 요망
//    public boolean isTokenValid(String token) {
//        try {
//            String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
//            SecretKey secretKey = Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
//
//            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
//            return true;
//        } catch (JwtException e) {
//            // 유효하지 않은 토큰이므로 false 반환
//            return false;
//        }
//    }

}
