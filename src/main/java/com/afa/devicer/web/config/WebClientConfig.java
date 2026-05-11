package com.afa.devicer.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    @Value("${app.backend.url}")
    private String backendUrl;

    @Bean
    public WebClient backendWebClient(final OAuth2AuthorizedClientManager authorizedClientManager) {

        final ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2.setDefaultOAuth2AuthorizedClient(true);

        return WebClient.builder()
                .baseUrl(backendUrl)
                .apply(oauth2.oauth2Configuration())
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(5 * 1024 * 1024))
                .build();
    }
}
