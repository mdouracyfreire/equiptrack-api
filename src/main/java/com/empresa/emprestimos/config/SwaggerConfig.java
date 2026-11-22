package com.empresa.emprestimos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Empréstimo de Equipamentos")
                        .version("1.0.0")
                        .description(
                                "API REST desenvolvida com Java 21 e Spring Boot 4 "
                                        + "para gerenciamento de equipamentos, colaboradores "
                                        + "e empréstimos. Permite cadastrar, consultar, "
                                        + "atualizar e excluir equipamentos, além de registrar "
                                        + "empréstimos, devoluções e consultar atrasos."
                        ))
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Informe o token JWT obtido no endpoint /auth/login.")
                        ))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME));
    }
}
