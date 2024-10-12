package cz.meind.synchro.synchrobackend.service.util;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final SynchroConfig config;

    public JwtUtil(SynchroConfig config) {
        this.config = config;
    }

    // Get the signing key
    private SecretKey getSigningKey() {
        byte[] keyBytes = config.getSecretKey().getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // Generate JWT token with additional claims
    public String generateToken(String username, Map<String, String> extraClaims, Long expiresIn) {
        return Jwts.builder().setClaims(extraClaims)  // Set extra claims
                .setSubject(username).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expiresIn)).signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Use new signWith method
                .compact();
    }

    // Extract claims from JWT token
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()  // Use parserBuilder for parsing
                .setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    // Validate JWT token
    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
