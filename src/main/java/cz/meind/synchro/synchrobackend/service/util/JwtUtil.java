package cz.meind.synchro.synchrobackend.service.util;

import cz.meind.synchro.synchrobackend.config.SynchroConfig;
import cz.meind.synchro.synchrobackend.database.repositories.BlacklistJwtRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;

@Service
public class JwtUtil {

    private final SynchroConfig config;
    private final BlacklistJwtRepository blacklistJwtRepository;

    public JwtUtil(SynchroConfig config, BlacklistJwtRepository blacklistJwtRepository) {
        this.config = config;
        this.blacklistJwtRepository = blacklistJwtRepository;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = config.getSecretKey().getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(String username, Map<String, String> extraClaims, Long expiresIn) {
        return Jwts.builder().setClaims(extraClaims)  // Set extra claims
                .setSubject(username).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expiresIn)).signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Use new signWith method
                .compact();
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }

    }

    public boolean isTokenValid(String token) {
        if (blacklistJwtRepository.findBlacklistJwtEntityByJwtToken(token).isPresent()) return false;
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
