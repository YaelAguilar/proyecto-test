package org.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.models.Role;
import org.example.models.User;

import java.util.Date;

public class JwtManager {
    private final Algorithm algorithm;
    private final String issuer = "marketplace-api";
    private final long expirationTimeMillis = 3600000; // 1 hora en ms

    public JwtManager(String secret) {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("JWT Secret cannot be null or empty.");
        }
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generateToken(User user, Role role) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(user.getId()))
                .withClaim("email", user.getEmail())
                .withClaim("fullName", user.getFullName())
                .withClaim("role", role.getRole())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeMillis))
                .sign(algorithm);
    }

    public DecodedJWT verifyToken(String token) throws JWTVerificationException {
        return JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
                .verify(token);
    }

    public Integer getUserIdFromToken(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            return Integer.parseInt(jwt.getSubject());
        } catch (JWTVerificationException | NumberFormatException e) {
            return null;
        }
    }

    public String getUserRoleFromToken(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            return jwt.getClaim("role").asString();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}