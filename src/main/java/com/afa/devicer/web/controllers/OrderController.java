package com.afa.devicer.web.controllers;

import com.afa.core.dto.employee.EmployeeSettingsResponse;
import com.afa.core.dto.orders.OrderPagedFilter;
import com.afa.core.dto.orders.OrderPagedResponse;
import com.afa.core.dto.orders.OrderSingleResponse;
import com.afa.core.enums.AmountTypes;
import com.afa.devicer.web.controllers.internal.ControllerConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@CrossOrigin
@RequestMapping(ControllerConstants.ORDERS)
@Tag(name = "orders", description = "Orders controller")
@Controller
@RequiredArgsConstructor
@SuppressWarnings({"PMD"})
public class OrderController extends BaseController {

    private final WebClient webClient;

    @GetMapping("/")
    public String list(final Model model) {

        final EmployeeSettingsResponse settings = webClient.get()
                .uri("/api/v8/employees/settings")
                .retrieve()
                .bodyToMono(EmployeeSettingsResponse.class)
                .block();

        final OrderPagedFilter orderPagedFilter = OrderPagedFilter.builder()
                .conditions(settings.getSettings().getOrders())
                .build();

        final OrderPagedResponse result = webClient.post()
                .uri("/api/v8/orders/full-filtered")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(orderPagedFilter)
                .retrieve()
                .bodyToMono(OrderPagedResponse.class)
                .block();

        populateDefaultModel(model);
        model.addAttribute("orders", result.getOrders());
        model.addAttribute("totalAmounts", result.getTotalAmounts());
        model.addAttribute("amountConversionBid", result.getTotalAmounts().get(AmountTypes.CONVERSION_BID));
        model.addAttribute("amountConversionApproved", result.getTotalAmounts().get(AmountTypes.CONVERSION_APPROVED));

        return "orders/list.html";
    }

    @GetMapping("/{orderId}/show")
    @Operation(summary = "Order по идентификатору")
    public String getOrder4Show(@NotNull @Valid @PathVariable final Long orderId,
                                final Model model) throws JsonProcessingException {

        final OrderSingleResponse result = webClient.get()
                .uri("/api/v8/orders/" + orderId)
                .retrieve()
                .bodyToMono(OrderSingleResponse.class)
                .block();

        populateDefaultModel(model);
        model.addAttribute("order", result.getOrder());

        return "orders/show.html";
    }

    @GetMapping("/{orderId}/update")
    @Operation(summary = "Order по идентификатору")
    public String getOrder4Edit(@NotNull @Valid @PathVariable final Long orderId,
                                final Model model) {


        populateDefaultModel(model);
        return "orders/orderForm.html";

    }

    @GetMapping("/{orderId}/change-status")
    @Operation(summary = "Order по идентификатору")
    public String getOrder4ChangeStatus(@NotNull @Valid @PathVariable final Long orderId,
                                        final Model model) {

        populateDefaultModel(model);
        return "orders/orderForm.html";
    }
}
