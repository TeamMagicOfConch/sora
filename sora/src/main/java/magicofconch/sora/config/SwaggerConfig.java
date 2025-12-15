package magicofconch.sora.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		String jwtSchemeName = "JWT Token";
		
		// JWT 보안 스키마 설정
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
		Components components = new Components()
			.addSecuritySchemes(jwtSchemeName, new SecurityScheme()
				.name(jwtSchemeName)
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT")
				.description("JWT 토큰을 입력해주세요 (Bearer 접두사 제외)")
			);

		return new OpenAPI()
			.info(new Info()
				.title("MagicOfConch API")
				.description("소라의 조언 - 사용자 API 문서")
				.version("v1.0.0")
			)
			.addSecurityItem(securityRequirement)
			.components(components);
	}
}
