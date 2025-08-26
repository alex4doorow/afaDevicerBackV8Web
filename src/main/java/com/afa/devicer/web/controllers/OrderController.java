package com.afa.devicer.web.controllers;

import com.afa.devicer.web.controllers.internal.ControllerConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@CrossOrigin
@RequestMapping(ControllerConstants.ORDERS)
@Tag(name = "orders", description = "Orders controller")
@Controller
public class OrderController {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final WebClient webClient;

    public OrderController(final OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8000")
                .build();
    }

    @GetMapping("/")
    public String list(final @AuthenticationPrincipal OidcUser oidcUser,
                       final OAuth2AuthenticationToken authToken,
                       final Model model) {

        if (oidcUser == null) {
            return "redirect:/login";
        }

        log.info("Пользователь: {}", oidcUser.getFullName());
        log.info("Email: {}", oidcUser.getEmail());

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                        authToken.getAuthorizedClientRegistrationId(),
                        authToken.getName()
                );

        // Получаем сам токен
        String accessToken = client.getAccessToken().getTokenValue();
        log.info("Access Token: {}", accessToken);

        final String requestBody = "{\n" +
                "    \"periodExist\": true,\n" +
                "    \"period\": {\n" +
                "        \"first\": \"2025-08-11\",\n" +
                "        \"second\": \"2025-08-11\"\n" +
                "    },\n" +
                "    \"types\": [\n" +
                "        \"ORDER\",\n" +
                "        \"BILL\"\n" +
                "    ],\n" +
                "    \"customerConditions\": {\n" +
                "        \"companyInn\": \"1234567890\"\n" +
                "    },\n" +
                "    \"productConditions\": {\n" +
                "        \"shortName\": \"\",\n" +
                "        \"sku\": \"\"\n" +
                "    },\n" +
                "    \"pageNumber\": 1,\n" +
                "    \"resultsOnPage\": 100\n" +
                "}";

        final String result = webClient.post()
                .uri("/api/v8/orders/full-filtered")
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();// можно async, но для примера синхронно

        log.info("result: {}", result);

        model.addAttribute("customPageTitle", "Заказы");
        return "orders/list.html";
    }
}
