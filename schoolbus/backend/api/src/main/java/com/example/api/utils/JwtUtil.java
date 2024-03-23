package com.example.api.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.shared.db.entities.Account;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Value;

public class JwtUtil {
    @Value("${security.jwt.token.secret-key}")
    private static String JWT_SECRET = "tieptd_194185";

    public static String generateAccessToken(Account account) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            return JWT.create()
                .withSubject(account.getId().toString())
                .withClaim("user_id", account.getId().toString())
                .withClaim("username", account.getUsername())
                .withClaim("role", account.getRole().toString())
                .withExpiresAt(genAccessExpirationDate())
                .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while generating token", exception);
        }
    }

    public static String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Error while validating token", exception);
        }
    }

    public static String generateRefreshToken(Account account) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            return JWT.create()
                .withSubject(account.getUsername())
                .withClaim("username", account.getUsername())
                .withExpiresAt(genRefreshExpirationDate())
                .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while refreshing token", exception);
        }
    }

    public static Instant genAccessExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("+07:00"));
    }

    public static Instant genRefreshExpirationDate() {
        return LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.of("+07:00"));
    }
}
