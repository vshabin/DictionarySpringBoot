package com.example.demo.domainservices;


import com.example.demo.domain.user.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class JwtProvider {

    private static final int ACCESS_TOKEN_EXPIRE_MINUTES = 5;
    private static final int REFRESH_TOKEN_EXPIRE_DAYS = 30;
    private final SecretKey ACCESS_SECRET;
    private final SecretKey REFRESH_SECRET;

    public JwtProvider(@Value("${jwt.secret.access}") String accessSecret, @Value("${jwt.secret.refresh}") String refreshSecret) {
        ACCESS_SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        REFRESH_SECRET = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
    }

    public String generateAccessToken(String login, String fullName,UUID userId, UUID sessionId, Role role) throws IOException {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusMinutes(ACCESS_TOKEN_EXPIRE_MINUTES).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts.builder()
                .setSubject(login)
                .claim("userId", userId.toString())
                //.claim("fullName",fullName)
                .claim("sessionId", sessionId.toString())
                .setExpiration(accessExpiration)
                .claim("role", role.toString())
                .signWith(ACCESS_SECRET)
                .compact();
    }

    public String generateRefreshToken(String login, String fullName, UUID userId, UUID sessionId) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(REFRESH_TOKEN_EXPIRE_DAYS).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(login)
                .claim("userId", userId.toString())
                //.claim("fullName",fullName)
                .claim("sessionId", sessionId.toString())
                .setExpiration(refreshExpiration)
                .claim("role", Role.REFRESH_TOKEN.toString())
                .signWith(REFRESH_SECRET)
                .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, ACCESS_SECRET);
    }
    public boolean validateAccessTokenData(String accessToken, UserDetails userDetails){
        String username = getClaims(accessToken,ACCESS_SECRET).getSubject();
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(accessToken,ACCESS_SECRET));
    }
    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, REFRESH_SECRET);
    }

    private boolean validateToken(String token, SecretKey secret) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException {
        Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token);
        return true;
    }

    public Claims getAccessClaims(String token) {
        return getClaims(token, ACCESS_SECRET);
    }

    public Claims getRefreshClaims(String token) {
        return getClaims(token, REFRESH_SECRET);
    }

    private Claims getClaims(String token, SecretKey secret) {
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token,SecretKey key) {
        return getClaims(token,key).getExpiration().before(new Date());
    }
}
