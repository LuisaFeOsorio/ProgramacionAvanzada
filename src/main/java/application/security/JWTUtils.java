package application.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JWTUtils {

    @Value("${jwt.secret:mySuperSecretKeyForJWTGenerationInSpringBootApplication2024}")
    private String secretKey;

    @Value("${jwt.expiration.hours:24}")
    private long expirationHours;

    public String generateToken(String email, Map<String, String> claims) {
        Instant now = Instant.now();

        JwtBuilder builder = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expirationHours, ChronoUnit.HOURS)))
                .signWith(getKey(), SignatureAlgorithm.HS256);

        // Agregar claims
        if (claims != null) {
            claims.forEach(builder::claim);
        }

        return builder.compact();
    }

    public Jws<Claims> parseJwt(String jwtString) throws ExpiredJwtException,
            UnsupportedJwtException, MalformedJwtException,
            SignatureException, IllegalArgumentException {

        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(jwtString);
    }

    public boolean validateToken(String token) {
        try {
            parseJwt(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            Jws<Claims> claimsJws = parseJwt(token);
            return claimsJws.getBody().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private SecretKey getKey() {
        // Validar que la clave tenga el tamaño mínimo requerido
        if (secretKey.length() < 32) {
            throw new IllegalStateException("JWT secret key must be at least 32 characters long");
        }
        byte[] secretKeyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }
}