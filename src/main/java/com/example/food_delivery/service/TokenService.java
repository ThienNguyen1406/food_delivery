package com.example.food_delivery.service;

import com.example.food_delivery.domain.model.TokenPayload;
import com.example.food_delivery.service.imp.TokenServiceImp;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import com.example.food_delivery.exception.AppException;
import com.example.food_delivery.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TokenService implements TokenServiceImp {
    @Value("${token.access.secret:dev-secret-please-change-32-characters-1234}")
    private String accessTokenSecret;

    @Value("${token.access.duration:PT15M}")
    private Duration accessTokenDuration;

    @Override
    public String generateAccessToken(TokenPayload payload) {

        Instant now = Instant.now();
        Instant expiration = now.plus(accessTokenDuration);

        payload.setIssuedAt(now);
        payload.setExpiredAt(expiration);

        return Jwts.builder()
                .setIssuedAt(Date.from(payload.getIssuedAt()))
                .setExpiration(Date.from(payload.getExpiredAt()))
                .setId(payload.getId())
                .claim("userId", payload.getUserId())
                .claim("roles", payload.getRoles())
                .signWith(secretKey())
                .compact();
    }

    @Override
    public TokenPayload validateAccessToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return new TokenPayload(
                    claims.getId(),
                    claims.get("userId", Long.class),
                    claims.getIssuedAt().toInstant(),
                    claims.getExpiration().toInstant(),
                    claims.get("roles", List.class)
            );
        } catch (JwtException e) {
            log.error("JwtException | {}", e.getMessage());
            throw new AppException(ErrorCode.UNCATEGORIZED);
        }
    }


    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
    }
}
