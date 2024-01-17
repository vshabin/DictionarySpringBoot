package com.example.demo.domainservices;


import com.example.demo.security.SecurityConst;
import com.example.demo.security.SecurityConst.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {
    @Value("${jwt.expire.access.minutes}")
    private int ACCESS_TOKEN_EXPIRE_MINUTES;
    @Value("${jwt.expire.refresh.days}")
    private int REFRESH_TOKEN_EXPIRE_DAYS;
    private final SecretKey ACCESS_SECRET;
    private final SecretKey REFRESH_SECRET;

    public JwtProvider(@Value("${jwt.secret.access}") String accessSecret, @Value("${jwt.secret.refresh}") String refreshSecret) {
        ACCESS_SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        REFRESH_SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
    }

    public String generateAccessToken(String login, UUID userId, UUID sessionId, Role role) throws IOException {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusMinutes(ACCESS_TOKEN_EXPIRE_MINUTES).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder().subject(login).claim(SecurityConst.USER_ID_CLAIM, userId.toString()).claim(SecurityConst.SESSION_ID_CLAIM, sessionId.toString()).expiration(accessExpiration).claim(SecurityConst.ROLE_CLAIM, role.toString()).signWith(ACCESS_SECRET).compact();
    }

    public String generateRefreshToken(String login, UUID userId, UUID sessionId) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(REFRESH_TOKEN_EXPIRE_DAYS).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder().subject(login).claim(SecurityConst.USER_ID_CLAIM, userId.toString()).claim(SecurityConst.SESSION_ID_CLAIM, sessionId.toString()).expiration(refreshExpiration).claim(SecurityConst.ROLE_CLAIM, Role.REFRESH_TOKEN.toString()).signWith(REFRESH_SECRET).compact();
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, ACCESS_SECRET);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, REFRESH_SECRET);
    }

    private boolean validateToken(String token, SecretKey secret) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException {
        Jwts.parser().verifyWith(secret).build().parseSignedClaims(token);
        return true;
    }

    public Claims getAccessClaims(String token) {
        return getClaims(token, ACCESS_SECRET);
    }

    public Claims getRefreshClaims(String token) {
        return getClaims(token, REFRESH_SECRET);
    }

    private Claims getClaims(String token, SecretKey secret) throws JwtException, IllegalArgumentException {
        return Jwts.parser().verifyWith(secret).build().parseSignedClaims(token).getPayload();
    }
}
