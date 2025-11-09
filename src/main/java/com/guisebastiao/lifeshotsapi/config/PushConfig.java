package com.guisebastiao.lifeshotsapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PushConfig {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    @Bean
    public WebClient expoWebClient() {
        return WebClient.builder()
                .baseUrl(EXPO_PUSH_URL)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                        .build())
                .build();
    }
}
