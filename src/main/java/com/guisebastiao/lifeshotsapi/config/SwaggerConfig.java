package com.guisebastiao.lifeshotsapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lifeshots API")
                        .version("1.0.0")
                        .description("Lifeshots is a social media platform that allows users to share moments through posts and stories, interact with friends, and engage with content in real time. The API provides endpoints for authentication, profile management, posts, stories, follows, comments, likes, and notifications.")
                        .license(new License()
                                .name("MIT License")
                                .url("https://github.com/guisebastiao/lifeshots-api")
                        )
                        .contact(new Contact()
                                .name("Gui Sebastião")
                                .email("guilhermesebastiaou.u@gmail.com")));
    }
}
