package magicofconch.sora.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.security.os_id.OsIdAuthenticationToken;
import magicOfConch.enums.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil {

    private SecretKey secretKey;


    @Value("${jwt.expiration.access}")
    private String accessTokenExpirationTime;

    @Value("${jwt.expiration.refresh}")
    private String refreshTokenExpirationTime;

    public long REFRESH_TOKEN_EXPIRATION;
    public long ACCESS_TOKEN_EXPIRATION;

    @PostConstruct
    public void init() {
        REFRESH_TOKEN_EXPIRATION = TimeUnit.MINUTES.toMillis(
                Integer.parseInt(refreshTokenExpirationTime));
        ACCESS_TOKEN_EXPIRATION = TimeUnit.MINUTES.toMillis(
                Integer.parseInt(accessTokenExpirationTime));
    }


    public JwtUtil(@Value("${jwt.secret}") String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));

            secretKey = new SecretKeySpec(hash, "HmacSHA256");

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found.", e);
        }
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    public String getUUID(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("uuid", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    public String generateAccessToken(String uuid, String role, String username) {
        return Jwts.builder()
                .claim("uuid", uuid)
                .claim("role", role)
                .claim("username", username)
                .claim("category", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String uuid, String role, String username) {
        return Jwts.builder()
                .claim("uuid", uuid)
                .claim("role", role)
                .claim("username", username)
                .claim("category", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    public TokenDto generateTokenDto(OsIdAuthenticationToken authentication) {

        String uuid = authentication.getUserDetails().getUuid();
        String username = authentication.getUserDetails().getUsername();

        String accessToken = generateAccessToken(uuid, UserRole.ROLE_USER.getRoleName(), username);
        String refreshToken = generateRefreshToken(uuid, UserRole.ROLE_USER.getRoleName(), username);

        return new TokenDto(accessToken, refreshToken);

    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }
}

