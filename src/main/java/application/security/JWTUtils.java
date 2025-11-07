package application.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JWTUtils {

    private final String SECRET_KEY = "mi-clave-secreta-muy-larga-para-jwt-256-bits-1234567890";

    public String generateToken(String id, Map<String, String> claims) {
        try {
            System.out.println("🔐 GENERANDO TOKEN JWT...");

            Instant now = Instant.now();
            SecretKey key = getKey();

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(id)
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            System.out.println("✅ TOKEN GENERADO - Longitud: " + token.length());
            return token;

        } catch (Exception e) {
            System.out.println("❌ ERROR GENERANDO TOKEN: " + e.getMessage());
            throw new RuntimeException("Error generando token JWT", e);
        }
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}