package com.chinuthon.project.uber.uber.Security;

import com.chinuthon.project.uber.uber.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secretKey}")
    public String JwtSecretKey;

    private SecretKey getSecretKey() {
        //Converts your string secret key into a SecretKey object using HMAC-SHA.
        return Keys.hmacShaKeyFor(JwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    // this token will get changed in some duration of time
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(String.valueOf(user.getId())) // Sets user ID as the subject
                .claim("email", user.getEmail())       // Custom claim for email
                .claim("roles", user.getRoles().toString()) // Custom claim for roles
                .issuedAt(new Date())                  // Sets issue time
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60*60)) // Expires in 5 minute
                .signWith(getSecretKey())              // Signs the token with secret key
                .compact();                            // Generates the final token (String)
    }

    // Permanent token to refresh access token
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(String.valueOf(user.getId())) // Sets user ID as the subject
                .issuedAt(new Date())                  // Sets issue time
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60L * 60L * 24L * 30L * 6L)) // Expires in ~6 months
                .signWith(getSecretKey())              // Signs the token with secret key
                .compact();                            // Generates the final token (String)
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.valueOf(claims.getSubject());   // Will return the user id from the token generated
    }
}
