package magicofconch.sora.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.security.os_id.OsIdAuthenticationToken;
import magicofconch.sora.user.enums.UserRole;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtUtil {

	private SecretKey secretKey;

	public static final long REFRESH_TOKEN_EXPIRATION = TimeUnit.DAYS.toMillis(1000);
	public static final long ACCESS_TOKEN_EXPIRATION = TimeUnit.MINUTES.toMillis(1);


	public JwtUtil(@Value("${jwt.secret}") String secret) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));

			secretKey = new SecretKeySpec(hash, "HmacSHA256");

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 algorithm not found.", e);
		}
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

	public String generateAccessToken(String uuid, String role) {
		return Jwts.builder()
			.claim("uuid", uuid)
			.claim("role", role)
			.claim("category", "access")
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
			.signWith(secretKey)
			.compact();
	}

	public String generateRefreshToken(String uuid, String role) {
		return Jwts.builder()
			.claim("uuid", uuid)
			.claim("role", role)
			.claim("category", "refresh")
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
			.signWith(secretKey)
			.compact();
	}

	public TokenDto generateTokenDto(OsIdAuthenticationToken authentication){

		String uuid = authentication.getUserDetails().getUuid();

		String accessToken = generateAccessToken(uuid, UserRole.ROLE_USER.getRoleName());
		String refreshToken =generateRefreshToken(uuid, UserRole.ROLE_USER.getRoleName());

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

