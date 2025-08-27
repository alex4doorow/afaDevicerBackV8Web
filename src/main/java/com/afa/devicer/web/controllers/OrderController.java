package com.afa.devicer.web.controllers;

import com.afa.core.dto.customers.CustomerConditionsDto;
import com.afa.core.dto.orders.OrderPagedFilter;
import com.afa.core.dto.orders.OrderPagedResponse;
import com.afa.core.dto.orders.OrderSingleResponse;
import com.afa.core.dto.products.ProductConditionsDto;
import com.afa.core.enums.AmountTypes;
import com.afa.core.enums.OrderTypes;
import com.afa.devicer.web.controllers.internal.ControllerConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Set;

@Slf4j
@CrossOrigin
@RequestMapping(ControllerConstants.ORDERS)
@Tag(name = "orders", description = "Orders controller")
@Controller
@SuppressWarnings({"PMD"})
public class OrderController extends BaseController {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public OrderController(final OAuth2AuthorizedClientService authorizedClientService,
                           final ObjectMapper objectMapper,
                           @Value("${app.backend.url}") final String appBackendUrl) {
        super();
        this.authorizedClientService = authorizedClientService;
        this.webClient = WebClient.builder()
                .baseUrl(appBackendUrl)
                .build();
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public String list(final @AuthenticationPrincipal OidcUser oidcUser,
                       final OAuth2AuthenticationToken authToken,
                       final Model model) throws JsonProcessingException {

        if (oidcUser == null) {
            return "redirect:/login";
        }

//        log.info("Пользователь: {}", oidcUser.getFullName());
//        log.info("Email: {}", oidcUser.getEmail());


        final OrderPagedFilter orderPagedFilter = OrderPagedFilter.builder()
                .periodExist(true)
                .period(Pair.of(LocalDate.of(2025, 8, 11), LocalDate.now()))
                .types(Set.of(OrderTypes.ORDER, OrderTypes.BILL))
                .customerConditions(CustomerConditionsDto.builder()
                        .companyInn("1234567890")
                        .build())
                .productConditions(ProductConditionsDto.builder()
                        .shortName("")
                        .sku("")
                        .build())
                .build();

        final OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authToken.getAuthorizedClientRegistrationId(),
                authToken.getName()
        );
        final String requestBody = objectMapper.writeValueAsString(orderPagedFilter);
        final String body = webClient.post()
                .uri("/api/v8/orders/full-filtered")
                .header("Authorization", "Bearer " + client.getAccessToken().getTokenValue())
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        final OrderPagedResponse result = objectMapper.readValue(body, OrderPagedResponse.class);

        populateDefaultModel(model);
        model.addAttribute("orders", result.getOrders());
        model.addAttribute("totalAmounts", result.getTotalAmounts());
        model.addAttribute("amountConversionBid", result.getTotalAmounts().get(AmountTypes.CONVERSION_BID));
        model.addAttribute("amountConversionApproved", result.getTotalAmounts().get(AmountTypes.CONVERSION_APPROVED));

        return "orders/list.html";
    }

    @GetMapping("/{orderId}/show")
    @Operation(summary = "Order по идентификатору")
    public String getOrder4Show(final @AuthenticationPrincipal OidcUser oidcUser,
                                final OAuth2AuthenticationToken authToken,
                                @NotNull @Valid @PathVariable final Long orderId,
                                final Model model) throws JsonProcessingException {

        if (oidcUser == null) {
            return "redirect:/login";
        }

        final OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authToken.getAuthorizedClientRegistrationId(),
                authToken.getName()
        );

        final String body = webClient.get()
                .uri("/api/v8/orders/" + orderId)
                .header("Authorization", "Bearer " + client.getAccessToken().getTokenValue())
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        final OrderSingleResponse result = objectMapper.readValue(body, OrderSingleResponse.class);

        populateDefaultModel(model);
        model.addAttribute("order", result.getOrder());

        return "orders/show.html";
    }

    @GetMapping("/{orderId}/update")
    @Operation(summary = "Order по идентификатору")
    public String getOrder4Edit(final @AuthenticationPrincipal OidcUser oidcUser,
                                final OAuth2AuthenticationToken authToken,
                                @NotNull @Valid @PathVariable final Long orderId,
                                final Model model) {

        if (oidcUser == null) {
            return "redirect:/login";
        }


        populateDefaultModel(model);
        return "orders/orderForm.html";

    }

    @GetMapping("/{orderId}/change-status")
    @Operation(summary = "Order по идентификатору")
    public String getOrder4ChangeStatus(final @AuthenticationPrincipal OidcUser oidcUser,
                                        final OAuth2AuthenticationToken authToken,
                                        @NotNull @Valid @PathVariable final Long orderId,
                                        final Model model) {

        if (oidcUser == null) {
            return "redirect:/login";
        }


        populateDefaultModel(model);
        return "orders/orderForm.html";

    }
}
