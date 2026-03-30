package com.ecommerce.product.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productApiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-commerce Product API")
                        .description("REST API for managing products in the e-commerce platform. " +
                                "Built with Spring Boot and Clean Architecture.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("E-commerce Team")
                                .email("team@ecommerce.com")));
    }
}
