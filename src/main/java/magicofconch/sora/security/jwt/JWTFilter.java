package magicofconch.sora.security.jwt;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import magicofconch.sora.security.CustomUserDetails;
import magicofconch.sora.security.UserDto;
import magicofconch.sora.security.os_id.OsIdAuthenticationToken;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	public JWTFilter (JwtUtil jwtUtil){
		this.jwtUtil = jwtUtil;
	}

	private final String AUTHORIZATION_HEADER = "Authorization";
	private final String REFRESH_TOKEN_HEADER = "Refresh-Token";
	private final String BEARER_PREFIX = "Bearer ";

	private final List<String> WHITE_LIST = List.of(
		"/test",
		"/user"
	);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		log.info("[JWTFilter] - access URI ={}", requestURI);
		if (WHITE_LIST.stream().anyMatch(requestURI::startsWith)) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken;

		try{
			accessToken = resolveAccessToken(request);
			jwtUtil.isExpired(accessToken);
		} catch(JwtException e){
			//response body
			PrintWriter writer = response.getWriter();
			writer.print("UnAuthorized");

			//response status code
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		String uuid = jwtUtil.getUUID(accessToken);
		String role = jwtUtil.getRole(accessToken);


		UserDto userDto = UserDto.builder()
			.role(role)
			.uuid(uuid)
			.build();

		CustomUserDetails userDetails = new CustomUserDetails(userDto);

		OsIdAuthenticationToken authToken = new OsIdAuthenticationToken(userDetails, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}

	private String resolveAccessToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if(bearerToken == null){
			throw new JwtException("no token");
		}

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.split(" ")[1].trim();
		}
		return null;
	}

	private String resolveRefreshToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(REFRESH_TOKEN_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.split(" ")[1].trim();
		}
		return null;
	}
}
