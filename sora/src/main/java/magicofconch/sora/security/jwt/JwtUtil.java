package magicofconch.sora.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import magicOfConch.enums.UserRole;
import magicofconch.sora.security.dto.res.TokenDto;
import magicofconch.sora.security.os_id.OsIdAuthenticationToken;

@Slf4j
@Component
public class JwtUtil {

	public long REFRESH_TOKEN_EXPIRATION;
	public long ACCESS_TOKEN_EXPIRATION;
	private SecretKey secretKey;
	@Value("${jwt.expiration.access}")
	private String accessTokenExpirationTime;
	@Value("${jwt.expiration.refresh}")
	private String refreshTokenExpirationTime;

	public JwtUtil(@Value("${jwt.secret}") String secret) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));

			secretKey = new SecretKeySpec(hash, "HmacSHA256");

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 algorithm not found.", e);
		}
	}

	@PostConstruct
	public void init() {
		REFRESH_TOKEN_EXPIRATION = TimeUnit.MINUTES.toMillis(
			Integer.parseInt(refreshTokenExpirationTime));
		ACCESS_TOKEN_EXPIRATION = TimeUnit.MINUTES.toMillis(
			Integer.parseInt(accessTokenExpirationTime));
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

	public String generateAccessToken(String uuid, UserRole role, String username) {
		return Jwts.builder()
			.claim("uuid", uuid)
			.claim("role", role.getRoleName())
			.claim("username", username)
			.claim("category", "access")
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
			.signWith(secretKey)
			.compact();
	}

	public String generateRefreshToken(String uuid, UserRole role, String username) {
		return Jwts.builder()
			.claim("uuid", uuid)
			.claim("role", role.getRoleName())
			.claim("username", username)
			.claim("category", "refresh")
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
			.signWith(secretKey)
			.compact();
	}

	public TokenDto generateUserTokenDto(OsIdAuthenticationToken authentication) {

		String uuid = authentication.getUserDetails().getUuid();
		String username = authentication.getUserDetails().getUsername();

		String accessToken = generateAccessToken(uuid, UserRole.ROLE_USER, username);
		String refreshToken = generateRefreshToken(uuid, UserRole.ROLE_USER, username);

		return new TokenDto(accessToken, refreshToken);
	}

	public TokenDto generateTokenDtoWithRole(OsIdAuthenticationToken authentication, UserRole userRole) {
		String uuid = authentication.getUserDetails().getUuid();
		String username = authentication.getUserDetails().getUsername();

		String accessToken = generateAccessToken(uuid, userRole, username);
		String refreshToken = generateRefreshToken(uuid, userRole, username);

		return new TokenDto(accessToken, refreshToken);
	}

	public TokenDto generateTokenDtoWithRole(String uuid, String username, UserRole userRole) {
		String accessToken = generateAccessToken(uuid, userRole, username);
		String refreshToken = generateRefreshToken(uuid, userRole, username);

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

