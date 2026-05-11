package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

public class JWTService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    private SecretKey generateSecertKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes((StandardCharsets.UTF_8)));
    }

    public String generateToke(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", Set.of("ADMIN", "USER"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000*60))
                .signWith(generateSecertKey())
                .compact();

    }
}
