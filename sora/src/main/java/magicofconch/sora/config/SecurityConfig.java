package magicofconch.sora.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import magicofconch.sora.security.jwt.JWTFilter;
import magicofconch.sora.security.jwt.JwtUtil;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtUtil jwtUtil;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// CORS 설정
		http.cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()));

		http.headers(
			headers -> headers.httpStrictTransportSecurity(hsts -> hsts.disable())  // HSTS 비활성화
		);

		// CSRF 비활성화
		http.csrf((auth) -> auth.disable());

		// 폼 로그인 및 기본 인증 비활성화
		http.formLogin((auth) -> auth.disable());
		http.httpBasic((auth) -> auth.disable());

		// 경로별 인가
		http.authorizeHttpRequests((auth) -> auth
			.requestMatchers("/test/api/**", "/user/**", "/swagger-ui.html",
				"/swagger-ui/**",
				"/api-docs/**",
				"/v3/api-docs/**",
				"/swagger-resources/**",
				"/webjars/**").permitAll()
			.requestMatchers("/auth/**").hasAnyRole("SEMI_USER", "USER")
			.requestMatchers("/auth/user/**").hasRole("USER")
			.anyRequest().authenticated());

		//stateless 설정
		http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Jwt 인증 필터 추가
		http
			.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	// CORS 설정
	private CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
		configuration.setAllowedMethods(Collections.singletonList("*"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Collections.singletonList("*"));
		configuration.setExposedHeaders(Arrays.asList("Authorization", "Refresh-Token"));
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
