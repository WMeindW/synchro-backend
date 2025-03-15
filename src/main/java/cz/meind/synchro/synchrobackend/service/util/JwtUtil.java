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

    /**
     * Constructor for JwtUtil.
     *
     * @param config                 Configuration object for retrieving the secret key and other configurations.
     * @param blacklistJwtRepository Repository for checking if a JWT token is blacklisted.
     */
    public JwtUtil(SynchroConfig config, BlacklistJwtRepository blacklistJwtRepository) {
        this.config = config;
        this.blacklistJwtRepository = blacklistJwtRepository;
    }

    /**
     * Retrieves the signing key for JWT token generation.
     * The signing key is derived from the configured secret key.
     *
     * @return The SecretKey used for signing JWT tokens.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = config.getSecretKey().getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * Generates a JWT token with the given username, additional claims, and expiration time.
     *
     * @param username    The username associated with the token.
     * @param extraClaims A map of extra claims to be included in the token.
     * @param expiresIn   The expiration time of the token in milliseconds.
     * @return The generated JWT token as a string.
     */
    public String generateToken(String username, Map<String, String> extraClaims, Long expiresIn) {
        return Jwts.builder().setClaims(extraClaims).setSubject(username) // Set the subject as the username
                .setIssuedAt(new Date()) // Set the current date as the issued date
                .setExpiration(new Date(System.currentTimeMillis() + expiresIn)) // Set the expiration date
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Sign the token using the signing key
                .compact(); // Return the compacted token string
    }

    /**
     * Extracts the claims from a JWT token.
     *
     * @param token The JWT token from which the claims will be extracted.
     * @return The Claims object containing the token's claims, or null if the token is invalid.
     */
    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSigningKey()) // Set the signing key to verify the token's signature
                    .build().parseClaimsJws(token).getBody(); // Parse the token and extract the claims
        } catch (Exception e) {
            return null; // Return null if there is an exception (e.g., token is invalid)
        }
    }

    /**
     * Checks if a JWT token is valid. A token is considered invalid if it is blacklisted or expired.
     *
     * @param token The JWT token to check.
     * @return True if the token is valid, otherwise false.
     */
    public boolean isTokenValid(String token) {
        if (blacklistJwtRepository.findBlacklistJwtEntityByJwtToken(token).isPresent())
            return false; // Check if token is blacklisted
        return !isTokenExpired(token); // Check if token is expired
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token The JWT token to check.
     * @return True if the token has expired, otherwise false.
     */
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date()); // Compare the expiration date with the current date
    }
}