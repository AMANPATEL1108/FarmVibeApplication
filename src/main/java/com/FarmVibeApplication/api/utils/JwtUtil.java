package com.FarmVibeApplication.api.utils;

import com.FarmVibeApplication.api.model.User;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "FarmVibeSecretKeyForJWTThatIsLongEnough"; // should be >= 256 bits for HS256
    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    private final long jwtExpirationInMs = 48 * 60 * 60 * 1000; // 48 hours

    private Key getSigningKey() {
        byte[] decodedKey = Base64.getEncoder().encode(SECRET.getBytes());
        return new SecretKeySpec(decodedKey, SIGNATURE_ALGORITHM.getJcaName());
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(Long.toString(user.getMobileNumber()))
                .claim("role", user.getRole())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SIGNATURE_ALGORITHM, getSigningKey())
                .compact();
    }

    public String getMobileFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException |
                 ExpiredJwtException | UnsupportedJwtException |
                 IllegalArgumentException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
