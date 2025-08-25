package com.example.book.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenAPIConfig{
    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .info(createAPIInfo())
                .servers(List.of(
                        createServer("http://localhost:8081", "Server URL in Development environment")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private Info createAPIInfo() {
        return new Info()
                .title("BookStore API")
                .version("1.0")
                .description("This API exposes all endpoints");
    }

    private Server createServer(String url, String description) {
        return new Server()
                .url(url)
                .description(description);
    }
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
    //https://stackoverflow.com/questions/59898874/enable-authorize-button-in-springdoc-openapi-ui-for-bearer-token-authentication

}