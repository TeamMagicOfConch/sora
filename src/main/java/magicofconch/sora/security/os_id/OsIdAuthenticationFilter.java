package magicofconch.sora.security.os_id;

import java.io.BufferedReader;
import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.security.CustomUserDetails;
import magicofconch.sora.security.jwt.JwtUtil;
import magicofconch.sora.user.enums.UserRole;
import magicofconch.sora.user.repository.OsAuthInfoRepository;
import magicofconch.sora.user.repository.UserInfoRepository;

@Slf4j
public class OsIdAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	private  final JwtUtil jwtUtil;

	public OsIdAuthenticationFilter(String defaultFilterProcessesUrl, JwtUtil jwtUtil) {
		super(defaultFilterProcessesUrl);
		this.jwtUtil = jwtUtil;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		super.setAuthenticationManager(authenticationManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException,
		IOException,
		ServletException {

		BufferedReader reader = request.getReader();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		String requestBody = sb.toString();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(requestBody);
		String osId = jsonNode.get("identifier").asText();

		log.info("[OsAuthenticationFilter - attemptAuthentication] - osId={}", osId);

		if (osId == null || osId.isEmpty()) {
			throw new AuthenticationServiceException("OS ID가 제공되지 않았습니다.");
		}

		OsIdAuthenticationToken authRequest = new OsIdAuthenticationToken(osId.trim());

		log.info("[OsAuthenticationFilter - attemptAuthentication] - before return osId={}", osId);
		return this.getAuthenticationManager().authenticate(authRequest);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
		throws IOException, ServletException {
		log.info("[OsIdAuthenticationFilter - successfulAuthentication] - string={}", authResult.getPrincipal());
		CustomUserDetails userDetails = (CustomUserDetails) authResult.getDetails();
		String uuid = userDetails.getUuid();  // UUID를 사용자 이름으로 사용한다고 가정

		String accessToken = jwtUtil.generateAccessToken(uuid, UserRole.ROLE_USER.getRoleName());
		String refreshToken = jwtUtil.generateRefreshToken(uuid, UserRole.ROLE_USER.getRoleName());

		response.setHeader("Access-Token", "Bearer " + accessToken);
		response.setHeader("Refresh-Token", "Bearer " + refreshToken);

		log.info("[OsIdAuthenticationFilter - successfulAuthentication] - accessToken={}", accessToken);
		log.info("[OsIdAuthenticationFilter - successfulAuthentication] - refreshToken={}", refreshToken);


		// 명시적으로 SecurityContext에 설정
		/*SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authResult);
		SecurityContextHolder.setContext(securityContext);*/
		log.info("[OsIdAuthenticationFilter - successfulAuthentication] - Authentication set in SecurityContext: {}", authResult);

		chain.doFilter(request, response);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
		throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

}
