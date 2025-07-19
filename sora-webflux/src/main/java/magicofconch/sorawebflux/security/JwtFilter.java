package magicofconch.sorawebflux.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    public static final String JWT_TOKEN_ATTRIBUTE = "jwt.token";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token) || jwtUtil.isExpired(token)) {
                log.warn("유효하지 않거나 만료된 JWT");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String role = jwtUtil.getRole(token);
            if (!"ROLE_USER".equalsIgnoreCase(role)) {
                log.warn("권한 부족: {}", role);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // context에 토큰 문자열만 저장
            exchange.getAttributes().put(JWT_TOKEN_ATTRIBUTE, token);
        }

        return chain.filter(exchange);
    }
}
