package magicofconch.soraadmin.util;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MagicOfConch Admin API")
                        .description("소라 어드민에서 사용하는 API 문서입니다.")
                        .version("v1.0.0")
                );
    }
}
