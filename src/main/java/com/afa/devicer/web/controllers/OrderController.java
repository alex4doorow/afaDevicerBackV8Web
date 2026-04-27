package com.afa.devicer.web.controllers;

import com.afa.core.dto.employee.EmployeeSettingsResponse;
import com.afa.core.dto.orders.*;
import com.afa.core.enums.AmountTypes;
import com.afa.core.enums.OrderPaymentTypes;
import com.afa.core.enums.OrderSourceTypes;
import com.afa.core.enums.OrderTypes;
import com.afa.devicer.web.controllers.internal.ControllerConstants;
import com.afa.devicer.web.dto.FormOrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@CrossOrigin
@RequestMapping(ControllerConstants.ORDERS)
@Tag(name = "orders", description = "Orders controller")
@Controller
@RequiredArgsConstructor
@SuppressWarnings({"PMD"})
public class OrderController extends BaseController {

    private final WebClient webClient;

    @GetMapping()
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
    public String getOrder4Show(
            @NotNull @Valid @PathVariable final Long orderId,
            final Model model) {

        final OrderSingleResponse result = webClient.get()
                .uri("/api/v8/orders/" + orderId)
                .retrieve()
                .bodyToMono(OrderSingleResponse.class)
                .block();

        populateDefaultModel(model);
        model.addAttribute("order", result.getOrder());
        model.addAttribute("listType", "typical");

        return "orders/show.html";
    }

    @GetMapping("/{orderId}/update")
    public String getOrder4Edit(
            @NotNull @Valid @PathVariable final Long orderId,
            final Model model) {

        populateDefaultModel(model);
        return "orders/orderForm.html";

    }

    @GetMapping("/{orderId}/change-status/{list-type}")
    public String getOrder4ChangeStatus(
            @NotNull @Valid @PathVariable final Long orderId,
            @PathVariable("list-type") String listType,
            final Model model) {

        final OrderSingleResponse response = webClient.get()
                .uri("/api/v8/orders/" + orderId)
                .retrieve()
                .bodyToMono(OrderSingleResponse.class)
                .block();
        final FormOrderDto form = new FormOrderDto(response.getOrder());

        populateDefaultModel(model);
        model.addAttribute("listType", listType);
        model.addAttribute("order", response.getOrder());
        model.addAttribute("formOrder", form);
        return "orders/orderStatusForm.html";
    }

    @PostMapping("/{orderId}/change-status/{list-type}")
    public String saveOrder4ChangeStatus(
            @NotNull @Valid @PathVariable final Long orderId,
            @PathVariable("list-type") String listType,
            @ModelAttribute("orderForm") @Validated FormOrderDto form,
            BindingResult bindingResult,
            Model model,
            final RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            final OrderSingleResponse response = webClient.get()
                    .uri("/api/v8/orders/" + orderId)
                    .retrieve()
                    .bodyToMono(OrderSingleResponse.class)
                    .block();
            model.addAttribute("listType", listType);
            model.addAttribute("order", response.getOrder());
            model.addAttribute("formOrder", form);
            return "orders/orderStatusForm.html";
        }

        final OrderChangeStatusSaveRequest request = OrderChangeStatusSaveRequest.builder()
                .type(OrderTypes.ORDER)
                .sourceType(OrderSourceTypes.LID)
                .paymentType(OrderPaymentTypes.PREPAYMENT)
                .productCategoryId(101L)
                .status(form.getOrderStatusType())
                .annotation(form.getAnnotation())
                .trackCode(form.getDelivery().getTrackCode())
                .build();
        final String uri = "/api/v8/orders/%d/change-status".formatted(orderId);
        final OrderDto result = webClient.patch()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrderDto.class)
                .block();
        log.info("{}", result.getId());
        return "redirect:/web/orders";
    }
}
