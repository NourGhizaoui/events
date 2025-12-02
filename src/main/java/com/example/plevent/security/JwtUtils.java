package com.example.plevent.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.Authentication;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private final Key key;

    public JwtUtils(@Value("${app.jwtSecret}") String jwtSecret) {
        // jwtSecret must be >= 32 chars
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Value("${app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    // ============================================================================
    // GENERATE TOKEN FROM AUTHENTICATION
    // ============================================================================
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    // ============================================================================
    // GENERATE TOKEN FROM USERNAME — NÉCESSAIRE POUR REFRESH TOKEN
    // ============================================================================
    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ============================================================================
    // GET USERNAME FROM TOKEN
    // ============================================================================
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ============================================================================
    // VALIDATE TOKEN
    // ============================================================================
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
