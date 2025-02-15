package com.digital_money_house.transactions_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Digital Money House API")
                        .version("1.0")
                        .description("Billetera virtual - Microservicio de Transacciones")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                )
                .servers(List.of(
                        new Server().url("http://localhost:8084").description("Servidor Local")
                        //new Server().url("https://mi-dominio.com").description("Producción")
                ));
    }
}

