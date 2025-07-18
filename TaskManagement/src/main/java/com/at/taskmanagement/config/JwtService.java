package com.at.taskmanagement.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey; // Import the more specific SecretKey
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Use the more specific and modern SecretKey type
    private SecretKey key;

    @PostConstruct
    public void init() {
        // The hmacShaKeyFor method already returns a SecretKey, so this is perfect.
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    /**
     * Generates a token using the modern, non-deprecated API.
     * The algorithm is automatically inferred from the SecretKey type.
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key) // Modern, non-deprecated method
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims using the modern, non-deprecated parser.
     * This resolves the warning you were seeing.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser() // The new parser is not built from a builder
                .verifyWith(key) // Replaces setSigningKey()
                .build()
                .parseSignedClaims(token) // Replaces parseClaimsJws()
                .getPayload(); // Replaces getBody()
    }
}
