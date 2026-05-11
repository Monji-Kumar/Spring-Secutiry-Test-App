package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

@Service
public class JWTService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    private SecretKey generateSecertKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes((StandardCharsets.UTF_8)));
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", Set.of("ADMIN", "USER"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60))
                .signWith(generateSecertKey())
                .compact();

    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(generateSecertKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("roles", Set.of("ADMIN", "USER"))
                .claim("type", "ACCESS")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*15))
                .signWith(generateSecertKey())
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("roles", Set.of("ADMIN", "USER"))
                .claim("type", "REFRESH")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60*60*24*7))
                .signWith(generateSecertKey())
                .compact();
    }

    public String getTokenType(String token) {
        Claims claim = Jwts.parser()
                        .verifyWith(generateSecertKey())
                        .build()
                .parseSignedClaims(token)
                .getPayload();

        return claim.get("type", String.class);
    }
}
