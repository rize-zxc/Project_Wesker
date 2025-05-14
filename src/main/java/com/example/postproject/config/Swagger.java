package com.example.postproject.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**class of swagger.*/
@Configuration
public class Swagger {
    /**realization of swagger.*/
    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local server"),
                        new Server().url("https://api.example.com").description("Production server")
                ))
                .info(new Info()
                        .title("Twister")
                        .version("1.0")
                        .description("Share your thoughts")
                        .contact(new Contact()
                                .name("Support")
                                .email("kira157322@.com")
                                .url("https://example.com/contact"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}