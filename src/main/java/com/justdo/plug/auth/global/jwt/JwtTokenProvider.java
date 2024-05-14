package com.justdo.plug.auth.global.jwt;


import com.justdo.plug.auth.global.exception.ApiException;
import com.justdo.plug.auth.global.response.code.status.ErrorStatus;
import com.justdo.plug.auth.global.utils.redis.RedisUtils;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;


    private final RedisUtils redisUtils;

    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 365L * 24 * 60 * 60 * 1000; // 1년
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 365L * 24 * 60 * 60 * 1000; // 1년

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

    // userId 추출
    public Long getUserIdFromToken(String token) {
        try {
            SecretKey secretKey = getSecretKey();

            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId",Long.class);

        } catch (JwtException e) {
            return null;
        }
    }

    public void validateRefreshToken(String refreshToken) {
        if (!isTokenValid(refreshToken)) {
            throw new ApiException(ErrorStatus._INVALID_REFRESH_TOKEN);
        }
    }

    public void validateStoredRefreshToken(String userId, String refreshToken) {
        String storedRefreshToken = redisUtils.getData(userId);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new ApiException(ErrorStatus._INVALID_REFRESH_TOKEN);
        }
    }

    public void deleteRefreshToken(String accessToken) {
        checkToken(accessToken);

        Long userId = extractUserIdFromToken(accessToken);

        try {
            redisUtils.deleteData(userId.toString());
        } catch (RedisConnectionException | DataAccessException e) {
            throw new ApiException(ErrorStatus._REDIS_OPERATION_ERROR);
        }

    }

    public void checkToken(String accessToken) {
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            throw new ApiException(ErrorStatus._UNAUTHORIZED);
        }
    }

    public Long extractUserIdFromToken(String jwtToken) {
        String jwt = jwtToken.substring(7);

        Long userId = getUserIdFromToken(jwt);

        if (userId == null) {
            throw new ApiException(ErrorStatus._UNAUTHORIZED);
        }

        return userId;
    }

    // 토큰 유효성 검사
    private boolean isTokenValid(String token) {
        try {
            SecretKey secretKey = getSecretKey();

            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            // 유효하지 않은 토큰이므로 false 반환
            return false;
        }
    }

}
