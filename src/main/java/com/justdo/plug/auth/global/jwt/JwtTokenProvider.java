package com.justdo.plug.auth.global.jwt;


import com.justdo.plug.auth.global.exception.ApiException;
import com.justdo.plug.auth.global.response.code.status.ErrorStatus;
import com.justdo.plug.auth.global.utils.RedisUtils;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.lettuce.core.RedisConnectionException;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refresh-expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    private final RedisUtils redisUtils;

    private SecretKey getSecretKey() {
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKey.getBytes());
        return Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }

    private Date getExpiryDate(long expirationTime) {
        return new Date(System.currentTimeMillis() + expirationTime);
    }

    public String generateAccessToken(Long memberId, Long blogId) {
        Date expiryDate = getExpiryDate(ACCESS_TOKEN_EXPIRATION_TIME);
        SecretKey secretKey = getSecretKey();

        return Jwts.builder()
                .subject("accessToken")
                .expiration(expiryDate)
                .claim("memberId", memberId)
                .claim("blogId", blogId)
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(Long memberId) {
        Date expiryDate = getExpiryDate(REFRESH_TOKEN_EXPIRATION_TIME);
        SecretKey secretKey = getSecretKey();

        String refreshToken = Jwts.builder()
                .subject("refreshToken")
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
        try {
            redisUtils.setData(memberId.toString(), refreshToken, REFRESH_TOKEN_EXPIRATION_TIME);
        } catch (RedisConnectionException | DataAccessException e) {
            throw new ApiException(ErrorStatus._REDIS_OPERATION_ERROR);
        }
        return refreshToken;
    }

    // userId 추출
    public Long getUserIdFromToken(String token) {
        try {
            SecretKey secretKey = getSecretKey();

            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                    .get("memberId", Long.class);

        } catch (JwtException e) {
            return null;
        }
    }

    public void validateRefreshToken(String refreshToken) {
        if (!isTokenValid(refreshToken)) {
            throw new ApiException(ErrorStatus._INVALID_REFRESH_TOKEN);
        }
    }

    public void validateStoredRefreshToken(String memberId, String refreshToken) {
        String storedRefreshToken = redisUtils.getData(memberId);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new ApiException(ErrorStatus._INVALID_REFRESH_TOKEN);
        }
    }

    public void deleteRefreshToken(String accessToken) {
        checkToken(accessToken);

        Long memberId = extractUserIdFromToken(accessToken);

        try {
            redisUtils.deleteData(memberId.toString());
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

        Long memberId = getUserIdFromToken(jwt);

        if (memberId == null) {
            throw new ApiException(ErrorStatus._UNAUTHORIZED);
        }

        return memberId;
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
