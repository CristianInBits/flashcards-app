package com.csindila.flashcards.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final String issuer;
    private final Key key;
    private final int expirationMinutes;

    public JwtService(
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expirationMinutes}") int expirationMinutes) {
        this.issuer = issuer;
        // Recomendación: secreto >= 32 bytes (256 bits). Si lo pasas Base64, decodifica
        // primero.
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public String generate(String subject, Map<String, Object> claims) {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        var exp = now.plusMinutes(expirationMinutes);
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(exp.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .requireIssuer(issuer)
                .setAllowedClockSkewSeconds(60) // tolerancia de reloj
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public long getExpirationSeconds() {
        return expirationMinutes * 60L;
    }
}